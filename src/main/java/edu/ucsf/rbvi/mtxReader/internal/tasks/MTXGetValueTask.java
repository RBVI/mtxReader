package edu.ucsf.rbvi.mtxReader.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.service.util.CyServiceRegistrar;

import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.mtxReader.internal.model.MatrixMarket;
import edu.ucsf.rbvi.mtxReader.internal.model.MTXManager;

public class MTXGetValueTask extends AbstractTask {
	final MTXManager mtxManager;

	@Tunable(description="Matrix to create table from")
	public ListSingleSelection<String> matrix = null;

	@Tunable(description="Transpose", tooltip="From rows=genes to rows=cells")
	public boolean transpose = false;

	@Tunable(description="Gene", tooltip="Gene")
	public String gene;

	@Tunable(description="Cell", tooltip="Cell")
	public String cell;

	public MTXGetValueTask(final MTXManager mtxManager) {
		this.mtxManager = mtxManager;
		List<String> matrices = new ArrayList<String>(mtxManager.getMatrixNames());
		if (matrices.size() > 0)
			matrix = new ListSingleSelection<String>(matrices);
	}

	@Override
	public void run(TaskMonitor taskMonitor) {
		if (matrix == null) return;
		MatrixMarket matrixMarket = mtxManager.getMatrix(matrix.getSelectedValue());
		if (matrixMarket == null) return;
		if (transpose) matrixMarket.setTranspose(transpose);
		double value = matrixMarket.getValue(gene, cell);
		if (!Double.isNaN(value))
			taskMonitor.showMessage(TaskMonitor.Level.INFO, ""+cell+":"+gene+" = "+value);
		else
			taskMonitor.showMessage(TaskMonitor.Level.WARN, ""+cell+":"+gene+" doesn't have a value");
	}
 
	@ProvidesTitle
	public String getTitle() {return "MTXCreateTable";}
}
