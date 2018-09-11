package edu.ucsf.rbvi.mtxReader.internal.tasks;

import java.io.InputStream;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.AbstractInputStreamTaskFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.mtxReader.internal.model.MTXManager;

public class MTXReaderTaskFactory extends AbstractInputStreamTaskFactory implements TaskFactory {
	final CyFileFilter mtxFilter;
	final CyServiceRegistrar cyRegistrar;
	final MTXManager mtxManager;

	public MTXReaderTaskFactory(final CyServiceRegistrar cyRegistrar, final CyFileFilter mtxFilter, final MTXManager manager) {
		super(mtxFilter);
		this.mtxFilter = mtxFilter;
		this.cyRegistrar = cyRegistrar;
		this.mtxManager = manager;
	}

	@Override
	public TaskIterator createTaskIterator(InputStream is, String inputName) {
		TaskIterator ti = new TaskIterator(new MTXReaderTask(cyRegistrar, is, inputName, mtxManager));
		return ti;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new MTXImporterTask(cyRegistrar, mtxManager));
	}

	@Override
	public boolean isReady() { return true; }

}
