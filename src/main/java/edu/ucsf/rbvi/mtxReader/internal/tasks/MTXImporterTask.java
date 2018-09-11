package edu.ucsf.rbvi.mtxReader.internal.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.io.read.AbstractCyNetworkReader;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import edu.ucsf.rbvi.mtxReader.internal.model.MatrixMarket;
import edu.ucsf.rbvi.mtxReader.internal.model.MTXManager;

public class MTXImporterTask extends AbstractTask {

	@Tunable (description="Matrix Market file", required=true, params="input=true", gravity=1.0)
	public File mtxFile;

	@Tunable (description="Row labels file", required=false, params="input=true", gravity=4.0)
	public File rowFile;

	@Tunable (description="Column labels file", required=false, params="input=true", gravity=5.0)
	public File colFile;

	final MTXImporter importer;

	final MTXManager mtxManager;

	public MTXImporterTask(final CyServiceRegistrar registrar, MTXManager mtxManager) {
		this.mtxManager = mtxManager;
		importer = new MTXImporter(registrar, mtxManager);
	}

	@Override
	public void run(TaskMonitor taskMonitor) {
		// Read our network from the MTX file
		MatrixMarket matrix = importer.readMTX(taskMonitor, mtxFile, rowFile, colFile);

		CyTable table = matrix.makeTable(mtxFile.getName(), true);

	}

	@ProvidesTitle
	public String getTitle() {return "MTX Importer";}
}
