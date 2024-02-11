package com.dataserve.se.services;

import java.lang.reflect.Constructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ibm.ecm.configuration.ConfigInterface;
import com.ibm.ecm.extension.PluginLogger;
import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONResponse;
import com.dataserve.se.db.command.Command;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Factory;
import com.filenet.api.security.User;

/**
 * Provides an abstract class that is extended to create a class implementing
 * each service provided by the plug-in. Services are actions, similar to
 * servlets or Struts actions, that perform operations on the IBM Content
 * Navigator server. A service can access content server application programming
 * interfaces (APIs) and Java EE APIs.
 * <p>
 * Services are invoked from the JavaScript functions that are defined for the
 * plug-in by using the <code>ecm.model.Request.invokePluginService</code>
 * function.
 * </p>
 * Follow best practices for servlets when implementing an IBM Content Navigator
 * plug-in service. In particular, always assume multi-threaded use and do not
 * keep unshared information in instance variables.
 */
public class AdvancedFileSearchService extends PluginService {

	/**
	 * Returns the unique identifier for this service.
	 * <p>
	 * <strong>Important:</strong> This identifier is used in URLs so it must
	 * contain only alphanumeric characters.
	 * </p>
	 * 
	 * @return A <code>String</code> that is used to identify the service.
	 */
	public String getId() {
		return "AdvancedFileSearchService";
	}

	/**
	 * Returns the name of the IBM Content Navigator service that this service
	 * overrides. If this service does not override an IBM Content Navigator
	 * service, this method returns <code>null</code>.
	 * 
	 * @returns The name of the service.
	 */
	public String getOverriddenService() {
		return null;
	}

	/**
	 * Performs the action of this service.
	 * 
	 * @param callbacks
	 *            An instance of the <code>PluginServiceCallbacks</code> class
	 *            that contains several functions that can be used by the
	 *            service. These functions provide access to the plug-in
	 *            configuration and content server APIs.
	 * @param request
	 *            The <code>HttpServletRequest</code> object that provides the
	 *            request. The service can access the invocation parameters from
	 *            the request.
	 * @param response
	 *            The <code>HttpServletResponse</code> object that is generated
	 *            by the service. The service can get the output stream and
	 *            write the response. The response must be in JSON format.
	 * @throws Exception
	 *             For exceptions that occur when the service is running. If the
	 *             logging level is high enough to log errors, information about
	 *             the exception is logged by IBM Content Navigator.
	 */
	public void execute(PluginServiceCallbacks callbacks, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		final PluginLogger log = callbacks.getLogger();
		String result = null;

		final String method = request.getParameter("method");
		if (method == null || method.trim().equals("")) {
			throw new Exception("The \"method\"  name parameter is missing or misspelled");
		}

		String currentUserId = getUserId(callbacks, request);

		request.setCharacterEncoding("UTF-8");
		request.setAttribute("callBacks", callbacks);
		request.setAttribute("curretUserId", currentUserId);

		Object commandInstance = null;
		try {
			Class<?> commandClass = Class.forName("com.dataserve.se.db.command." + method);
			Constructor<?> construct = commandClass.getConstructor(HttpServletRequest.class);
			commandInstance = construct.newInstance(request);

			if (!(commandInstance instanceof Command)) {
				throw new Exception("Provided class does not implement Command insterface");
			}

			request.setCharacterEncoding("utf-8");
			Command comm = (Command) commandInstance;
			if (comm.checkAccess()) {
				result = comm.execute();
			} else {
				result = "ERROR (ACCESS DENIED): Missing permission for user: " + currentUserId + ", " + comm.getAccessDescription();
			}



			log.logDebug(this, method, " method has been executed");

		} catch (ClassNotFoundException e) {
			log.logError(this, method, e);
			result = "ERROR: Passed method '" + method + "' name does not match any existing command!" + e.getMessage();
		} catch (NoSuchMethodException e) {
			log.logError(this, method, e);
			result = "ERROR: Required constructor is available!" + e.getMessage();
		} catch (InstantiationException e) {
			log.logError(this, method, e);
			result = "ERROR: Instanciating the command class '" + method + "' failed" + e.getMessage();
		} catch (IllegalAccessException e) {
			log.logError(this, method, e);
			result = "ERROR: Error accessing the command '" + method + "' class/method" + e.getMessage();
		} catch (Exception e) {
			log.logError(this, method, e);
			result = "ERROR: " + e.getMessage();
		}

		JSONResponse jsonResults = new JSONResponse();
		jsonResults.put("result", result);
		jsonResults.serialize(response.getOutputStream());
	}

	

	public String getUserId(PluginServiceCallbacks callbacks, HttpServletRequest request) {
		try {
			return callbacks.getUserId();
		} catch (NullPointerException e) {
			ConfigInterface configInterface = callbacks.getConfigInterface();
			String repositoryId = configInterface.getDesktopConfig(request).getDefaultRepositoryId();
			Connection connection = callbacks.getP8Connection(repositoryId);
			if (connection != null) {
				User currentUser = Factory.User.fetchCurrent(connection, null);
				return currentUser.get_ShortName();
			}
			return null;
		}
	}
}
