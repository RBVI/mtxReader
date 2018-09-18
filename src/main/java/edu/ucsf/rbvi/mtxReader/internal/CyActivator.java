package edu.ucsf.rbvi.mtxReader.internal;

import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.ID;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_BEFORE;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.io.BasicCyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskFactory;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ucsf.rbvi.mtxReader.internal.model.MTXManager;
import edu.ucsf.rbvi.mtxReader.internal.tasks.MTXCreateTableTaskFactory;
import edu.ucsf.rbvi.mtxReader.internal.tasks.MTXGetValueTaskFactory;
import edu.ucsf.rbvi.mtxReader.internal.tasks.MTXReaderTaskFactory;

public class CyActivator extends AbstractCyActivator {

	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {
		final StreamUtil streamUtil = getService(bc, StreamUtil.class);
		final CyServiceRegistrar serviceRegistrar = getService(bc, CyServiceRegistrar.class);

		final MTXManager manager = new MTXManager();

		{
			// This is for the basic reader.  Note that we'll also load a more advanced one below
			final BasicCyFileFilter mtxFileFilter = new BasicCyFileFilter(new String[] { "mtx" },
			                              new String[] { "application/mtx" }, "MTX", DataCategory.TABLE, streamUtil);
			final MTXReaderTaskFactory mtxReaderFactory = new MTXReaderTaskFactory(serviceRegistrar, mtxFileFilter, manager);
	
			Properties mtxReaderProps = new Properties();
			mtxReaderProps.put(ID, "mtxTableReaderFactory");
			registerService(bc, mtxReaderFactory, InputStreamTaskFactory.class, mtxReaderProps);
	
			Properties mtxImporterProps = new Properties();
			mtxImporterProps.setProperty(PREFERRED_MENU, "Apps.MTXImporter");
			mtxImporterProps.setProperty(TITLE, "Import MTX files");
			registerService(bc, mtxReaderFactory, TaskFactory.class, mtxImporterProps);
		}

		{
			final MTXCreateTableTaskFactory mtxCreateTableFactory = new MTXCreateTableTaskFactory(serviceRegistrar, manager);
			Properties mtxTableProps = new Properties();
			mtxTableProps.setProperty(PREFERRED_MENU, "Apps.MTXImporter");
			mtxTableProps.setProperty(TITLE, "Create table from MTX file");
			registerService(bc, mtxCreateTableFactory, TaskFactory.class, mtxTableProps);
		}

		{
			final MTXGetValueTaskFactory mtxGetValueTaskFactory = new MTXGetValueTaskFactory(serviceRegistrar, manager);
			Properties mtxGetValueProps = new Properties();
			mtxGetValueProps.setProperty(PREFERRED_MENU, "Apps.MTXImporter");
			mtxGetValueProps.setProperty(TITLE, "Get value from matrix");
			registerService(bc, mtxGetValueTaskFactory, TaskFactory.class, mtxGetValueProps);
		}

	}
}
