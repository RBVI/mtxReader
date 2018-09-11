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

public class MTXCreateTableTask extends AbstractTask {
	final MTXManager mtxManager;

	public MTXCreateTableTask(final MTXManager mtxManager) {
		this.mtxManager = mtxManager;
	}

	@Override
	public void run(TaskMonitor taskMonitor) {
	}
 
	@ProvidesTitle
	public String getTitle() {return "MTXCreateTable";}
}
