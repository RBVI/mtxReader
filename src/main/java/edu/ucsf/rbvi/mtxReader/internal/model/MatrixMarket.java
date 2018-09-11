package edu.ucsf.rbvi.mtxReader.internal.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskMonitor;

public class MatrixMarket {
	public static String HEADER = "%%MatrixMarket";
	public static String COMMENT = "%";
	public static String delimiter = null;

	public static enum MTXOBJECT {
		MATRIX("matrix"),
		// DGRAPH("directed graph"),
		VECTOR("vector");
	
		String strOType;
		MTXOBJECT(String type) {
			this.strOType = type;
		}

		public String toString() { return strOType; }

		public static MTXOBJECT getEnum(String str) {
			for (MTXOBJECT obj: MTXOBJECT.values()) {
				if (str.toLowerCase().equals(obj.toString()))
					return obj;
			}
			return null;
		}
	}


	public enum MTXFORMAT {
		COORDINATE("coordinate"),
		ARRAY("array");
	
		String strFormat;
		MTXFORMAT(String format) {
			this.strFormat = format;
		}

		public String toString() { return strFormat; }

		public static MTXFORMAT getEnum(String str) {
			for (MTXFORMAT obj: MTXFORMAT.values()) {
				if (str.toLowerCase().equals(obj.toString()))
					return obj;
			}
			return null;
		}
	}

	public enum MTXTYPE {
		REAL("real"),
		COMPLEX("complex"),
		INTEGER("integer"),
		PATTERN("pattern");
	
		String strType;
		MTXTYPE(String type) {
			this.strType = type;
		}

		public String toString() { return strType; }

		public static MTXTYPE getEnum(String str) {
			for (MTXTYPE obj: MTXTYPE.values()) {
				if (str.toLowerCase().equals(obj.toString()))
					return obj;
			}
			return null;
		}
	}

	public enum MTXSYMMETRY {
		GENERAL("general"),
		SYMTXETRIC("symmetric"),
		SKEW("skew-symmetric"),
		HERMITIAN("hermitian");

		String strSymmetry;
		MTXSYMMETRY(String sym) {
			this.strSymmetry = sym;
		}

		public String toString() { return strSymmetry; }

		public static MTXSYMMETRY getEnum(String str) {
			for (MTXSYMMETRY obj: MTXSYMMETRY.values()) {
				if (str.toLowerCase().equals(obj.toString()))
					return obj;
			}
			return null;
		}
	}


	// Information about the matrix
	MTXOBJECT objectType;
	MTXFORMAT format;
	MTXTYPE type;
	MTXSYMMETRY sym;
	boolean transposed = false;

	List<String> comments;

	int nRows;
	int nCols;
	int nonZeros;

	List<String[]> rowLabels;
	List<String[]> colLabels;

	// We only support real and integer at this point
	int[][] intMatrix;
	double[][] doubleMatrix;

	private final CyServiceRegistrar registrar;
	private final CyTableFactory tableFactory;
	private final CyTableManager tableManager;

	public MatrixMarket(final CyServiceRegistrar registrar) {
		this(registrar, null, null);
	}

	public MatrixMarket(final CyServiceRegistrar registrar, 
	                    List<String[]> rowLabels, List<String[]> colLabels) {
		this.registrar = registrar;
		this.rowLabels = rowLabels;
		this.colLabels = colLabels;
		this.tableFactory = registrar.getService(CyTableFactory.class);
		this.tableManager = registrar.getService(CyTableManager.class);
	}

	public MTXFORMAT getFormat() {
		return format;
	}

	public MTXOBJECT getObjectType() {
		return objectType;
	}

	public MTXTYPE getType() {
		return type;
	}

	public MTXSYMMETRY getSymmetry() {
		return sym;
	}

	public int getNCols() { return transposed ? nRows : nCols; }
	public int getNRows() { return transposed ? nCols : nRows; }
	public int getNonZeroCount() { return nonZeros; }
	public boolean isTransposed() { return transposed; }
	public void setTranspose(boolean t) { transposed = t; }
	public List<String[]> getRowLabels() { return rowLabels; }
	public void setRowLabels(List<String[]> rLabels) { rowLabels = rLabels; }
	public List<String[]> getColLabels() { return colLabels; }
	public void setColLabels(List<String[]> cLabels) { colLabels = cLabels; }

	public void readMTX(TaskMonitor taskMonitor, File mmInputName) throws FileNotFoundException, IOException {
		FileInputStream inputStream = new FileInputStream(mmInputName);
		readMTX(taskMonitor, inputStream, mmInputName.getName());
	}

	public void readMTX(TaskMonitor taskMonitor, InputStream stream, String mmInputName) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		// Read the first line
		String header = reader.readLine();
		parseHeader(header);

		// Now, read until we find the dimensions
		comments = new ArrayList<>();
		String line = reader.readLine();
		while (line.startsWith(COMMENT) || line.trim().length() == 0) {
			if (line.startsWith(COMMENT))
				comments.add(line);
			line = reader.readLine();
		}

		// at this point, line should have our dimensions.
		// if we have a coordinate, we want three values, otherwise we want two
		String[] dims = line.split("\\s+");
		nRows = Integer.parseInt(dims[0]);
		nCols = Integer.parseInt(dims[1]);
		if (format == MTXFORMAT.ARRAY) {
			if (type == MTXTYPE.REAL) {
				doubleMatrix = new double[nRows][nCols];
			} else if (type == MTXTYPE.INTEGER) {
				intMatrix = new int[nRows][nCols];
			}

			for (int col = 0; col < nCols; col++) {
				for (int row = 0; row < nRows; row++) {
					readArrayLine(row, col, reader);
				}
			}
		} else if (format == MTXFORMAT.COORDINATE) {
			nonZeros = Integer.parseInt(dims[2]);
			if (type == MTXTYPE.INTEGER) {
				intMatrix = new int[nonZeros][3];
			} else if (type == MTXTYPE.REAL) {
				intMatrix = new int[nonZeros][2]; // indices
				doubleMatrix = new double[nonZeros][1]; // actual data
			}
			for (int index = 0; index < nonZeros; index++) {
				readCoordinateLine(index, reader);
			}
		}
	}

	public void mergeTable(CyTable table, String mergeColumn) {
	}

	public CyTable makeTable(String name, boolean addTable) {
		// Create the table
		CyTable table;
		if (transposed) {
			table = tableFactory.createTable(name, "Cells", String.class, true, false);
			createColumns(table, rowLabels);
			createRows(table, colLabels, rowLabels);
		} else {
			table = tableFactory.createTable(name, "Genes", String.class, true, false);
			createColumns(table, colLabels);
			System.out.println("Created columns");
			createRows(table, rowLabels, colLabels);
			System.out.println("Created rows");
		}

		if (addTable) {
			System.out.println("Adding table to table manager");
			tableManager.addTable(table);
			System.out.println("...done...");
		}
		return table;
	}

	public int[][] getIntegerMatrix(int missing) {
		if (format == MTXFORMAT.ARRAY) {
			if (type == MTXTYPE.INTEGER)
				return intMatrix;

			if (type == MTXTYPE.REAL) {
				int[][] newArray = new int[getNRows()][getNCols()];
				for (int row = 0; row < nRows; row++) {
					for (int col = 0; col < nCols; col++) {
						if (transposed)
							newArray[col][row] = (int)Math.round(doubleMatrix[col][row]);
						else
							newArray[row][col] = (int)Math.round(doubleMatrix[row][col]);
					}
				}
				return newArray;
			}
		} else if (format == MTXFORMAT.COORDINATE) {
			int[][] newArray = new int[getNRows()][getNCols()];
			int maxFill = getNRows();
			for (int row = 0; row < maxFill; row++) {
				Arrays.fill(newArray[row], missing);
			}
			for (int index = 0; index < nonZeros; index++) {
				int row = intMatrix[index][0];
				int col = intMatrix[index][1];
				if (transposed) { int rtmp = row; row = col; col = rtmp; }
				if (type == MTXTYPE.INTEGER)
					newArray[row-1][col-1] = intMatrix[index][2];
				else if (type == MTXTYPE.REAL)
					newArray[row-1][col-1] = (int)Math.round(doubleMatrix[index][0]);
			}
			return newArray;
		}
		return null;
	}

	public double[][] getDoubleMatrix(double missing) {
		if (format == MTXFORMAT.ARRAY) {
			if (type == MTXTYPE.REAL)
				return doubleMatrix;

			if (type == MTXTYPE.INTEGER) {
				double[][] newArray = new double[getNRows()][getNCols()];
				for (int row = 0; row < nRows; row++) {
					for (int col = 0; col < nCols; col++) {
						if (transposed)
							newArray[col][row] = (double)intMatrix[col][row];
						else
							newArray[row][col] = (double)intMatrix[row][col];
					}
				}
				return newArray;
			}
		} else if (format == MTXFORMAT.COORDINATE) {
			double[][] newArray = new double[getNRows()][getNCols()];
			int maxFill = getNRows();
			for (int row = 0; row < maxFill; row++) {
				Arrays.fill(newArray[row], missing);
			}
			for (int index = 0; index < nonZeros; index++) {
				int row = intMatrix[index][0];
				int col = intMatrix[index][1];
				if (transposed) { int rtmp = row; row = col; col = rtmp; }
				if (type == MTXTYPE.INTEGER)
					newArray[row-1][col-1] = (double)intMatrix[index][2];
				else if (type == MTXTYPE.REAL)
					newArray[row-1][col-1] = doubleMatrix[index][0];
			}
			return newArray;
		}
		return null;
	}

	private void parseHeader(String header) throws IOException {
		if (!header.startsWith(HEADER))
			throw new IOException("File doesn't start with appropriate header");

		String[] headerArgs = header.split("\\s+");

		objectType = MTXOBJECT.getEnum(headerArgs[1]);
		if (objectType == null)
			throw new IOException("Illegal or unsupported object type: "+headerArgs[1]);
		if (objectType == MTXOBJECT.VECTOR)
			throw new IOException("Vector objects are not supported at this time");

		format = MTXFORMAT.getEnum(headerArgs[2]);
		if (format == null)
			throw new IOException("Illegal or unsupported format: "+headerArgs[2]);

		type = MTXTYPE.getEnum(headerArgs[3]);
		if (type == null)
			throw new IOException("Illegal or unsupported type: "+headerArgs[3]);
		if (type == MTXTYPE.COMPLEX || type == MTXTYPE.PATTERN)
			throw new IOException("Complex and pattern types are not supported at this time");

		sym = MTXSYMMETRY.getEnum(headerArgs[4]);
		if (sym == null)
			throw new IOException("Illegal or unsupported symmetry: "+headerArgs[4]);
		if (sym == MTXSYMMETRY.HERMITIAN)
			throw new IOException("Hermitian symmetry is not supported at this time");
	}

	private void readArrayLine(int row, int col, BufferedReader reader) throws IOException {
		String line = reader.readLine();
		// Skip over blank lines
		while (line.trim().length() == 0)
			line = reader.readLine();
		if (type == MTXTYPE.INTEGER) {
			intMatrix[row][col] = Integer.parseInt(line.trim());
		} else if (type == MTXTYPE.REAL) {
			doubleMatrix[row][col] = Double.parseDouble(line.trim());
		}
	}

	private void readCoordinateLine(int index, BufferedReader reader) throws IOException {
		String line = reader.readLine();
		// Skip over blank lines
		while (line.trim().length() == 0)
			line = reader.readLine();

		// We should have exactly three value: row, col, value
		String[] vals = line.split("\\s+");
		intMatrix[index][0] = Integer.parseInt(vals[0]);
		intMatrix[index][1] = Integer.parseInt(vals[1]);
		if (type == MTXTYPE.INTEGER) {
			intMatrix[index][2] = Integer.parseInt(vals[2]);
		} else if (type == MTXTYPE.REAL) {
			doubleMatrix[index][0] = Double.parseDouble(vals[2]);
		}
	}

	private void createColumns(CyTable table, List<String[]> labels) {
		for (String[] lbl: labels) {
			String columnLabel = "MTX::"+lbl[1];
			table.createColumn(columnLabel, Double.class, true);
		}
	}

	private void createRows(CyTable table, List<String[]> rowLabels, List<String[]> colLabels) {
		if (format == MTXFORMAT.COORDINATE) {
			for (int index = 0; index < nonZeros; index++) {
				int row = intMatrix[index][0];
				int col = intMatrix[index][1];
				if (transposed) { int rtmp = row; row = col; col = rtmp; }
				String rowLabel = rowLabels.get(row-1)[1];
				CyRow cyRow = table.getRow(rowLabel);
				String colLabel = "MTX::"+colLabels.get(col-1)[1];
				if (type == MTXTYPE.REAL) {
					double v = doubleMatrix[index][0];
					cyRow.set(colLabel, Double.valueOf(v));
				} else if (type == MTXTYPE.INTEGER) {
					int v = intMatrix[index][2];
					cyRow.set(colLabel, Double.valueOf(v));
				}
			}
		} else if (format == MTXFORMAT.ARRAY) {
			for (int row = 0; row < rowLabels.size(); row++) {
				String rowLabel = rowLabels.get(row)[1];
				CyRow cyRow = table.getRow(rowLabel);
				for (int col = 0; col < colLabels.size(); col++) {
					String colLabel = "MTX::"+colLabels.get(col)[1];
					int r = row+1;
					int c = col+1;
					if (transposed) { int rtmp = r; r = c; c = rtmp; }
					if (type == MTXTYPE.REAL) {
						cyRow.set(colLabel, Double.valueOf(doubleMatrix[r][c]));
					} else if (type == MTXTYPE.INTEGER) {
						cyRow.set(colLabel, Double.valueOf(intMatrix[r][c]));
					}
				}
			}
		}
	}
}