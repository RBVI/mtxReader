package edu.ucsf.rbvi.mtxReader.internal.tasks;

import java.io.InputStream;

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.mtxReader.internal.model.MTXManager;

public class MTXGetValueTaskFactory extends AbstractTaskFactory {
	final CyServiceRegistrar cyRegistrar;
	final MTXManager mtxManager;

	public MTXGetValueTaskFactory(final CyServiceRegistrar cyRegistrar, final MTXManager mtxManager) {
		super();
		this.cyRegistrar = cyRegistrar;
		this.mtxManager = mtxManager;
	}

	@Override
	public TaskIterator createTaskIterator() {
		TaskIterator ti = new TaskIterator(new MTXGetValueTask(mtxManager));
		return ti;
	}

	@Override
	public boolean isReady() { return true; }

}
