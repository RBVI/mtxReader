package edu.ucsf.rbvi.mtxReader.internal.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
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

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;

import edu.ucsf.rbvi.mtxReader.internal.model.MTXManager;

public class MTXReaderTask extends AbstractTask {
	private final MTXImporter mtxImporter;
	private final String inputName;
	private final InputStream stream;
	public final MTXManager mtxManager;

	public MTXReaderTask(final CyServiceRegistrar registrar,
	                     final InputStream stream, final String name,
											 final MTXManager mtxManager){
		this.inputName = name;
		this.stream = stream;
		this.mtxManager = mtxManager;
		mtxImporter = new MTXImporter(registrar, mtxManager);
	}

	@Override
	public void run(TaskMonitor taskMonitor) {
		mtxImporter.readMTX(taskMonitor, stream, inputName);
	}
 
	@ProvidesTitle
	public String getTitle() {return "MTXReader";}
}
