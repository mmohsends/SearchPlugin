package com.dataserve.se.services;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import java.util.zip.GZIPOutputStream;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dataserve.se.db.command.FileNetDateConverter;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;
import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONMessage;
import com.ibm.ecm.json.JSONResultSetResponse;
import com.ibm.json.java.JSONObject;

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
public class LinkSearchService  extends PluginService {

	public static final String REPOSITORY_ID = "repositoryId";
	public static final String REPOSITORY_TYPE = "repositoryType";
	public static final String SEARCH_WORD= "searchWord"; 
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
		return "LinkSearchService";
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
	public void execute(PluginServiceCallbacks callbacks,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {		String methodName = "execute";
			callbacks.getLogger().logEntry(this, methodName, request);

			String repositoryId = request.getParameter(REPOSITORY_ID);
			String repositoryType = request.getParameter(REPOSITORY_TYPE);
			String searchWord = request.getParameter(SEARCH_WORD);
			String query = buildSearchQuery(searchWord);
			ObjectStore os = callbacks.getP8ObjectStore(repositoryId);
			JSONResultSetResponse jsonResults = new JSONResultSetResponse();
			jsonResults.setPageSize(50);

			try {
				if (repositoryType.equals("p8")) {
					Subject subject = callbacks.getP8Subject(repositoryId);
					UserContext.get().pushSubject(subject);
				}

				Object synchObject = callbacks.getSynchObject(repositoryId, repositoryType);
				if (synchObject != null) {
					synchronized (synchObject) {
						 if (repositoryType.equals("p8")) {
							 SamplePluginLinkSearchServiceP8.executeP8Search(request, repositoryId, query, callbacks, jsonResults, request.getLocale());
						}
					}
				} else {
					 if (repositoryType.equals("p8")) {
						 SamplePluginLinkSearchServiceP8.executeP8Search(request, repositoryId, query, callbacks, jsonResults, request.getLocale());
					}
				}

				// Write results to response
				writeResponse(request, response, jsonResults);

			} catch (Exception e) {
				// provide error information
				callbacks.getLogger().logError(this, methodName, request, e);

				JSONMessage jsonMessage = new JSONMessage(0, e.getMessage(), "This error may occur if the search string is invalid.", "Ensure the search string is the correct syntax.", "Check the IBM Content Navigator logs for more details.", "");
				jsonResults.addErrorMessage(jsonMessage);
				writeResponse(request, response, jsonResults);
			} finally {
				if (repositoryType.equals("p8")) {
					UserContext.get().popSubject();
				}

				callbacks.getLogger().logExit(this, methodName, request);
			}
		}

		

		private void writeResponse(HttpServletRequest request, HttpServletResponse response, JSONResultSetResponse json) throws Exception {
			Writer writer = null;

			try {
				// Prevent browsers from returning cached response on subsequent request
				response.addHeader("Cache-Control", "no-cache");

				// GZip JSON response if client supports it
				String acceptedEncodings = request.getHeader("Accept-Encoding");
				if (acceptedEncodings != null && acceptedEncodings.indexOf("gzip") >= 0) {
					if (!response.isCommitted())
						response.setBufferSize(65536); // since many times response is larger than default buffer (4096)
					response.setHeader("Content-Encoding", "gzip");
					response.setContentType("text/plain"); // must be text/plain for firebug
					GZIPOutputStream gzos = new GZIPOutputStream(response.getOutputStream());
					writer = new OutputStreamWriter(gzos, "UTF-8");
					// Add secure JSON prefix
					writer.write("{}&&");
					writer.flush();
					json.serialize(writer);
				} else {
					response.setContentType("text/plain"); // must be text/plain for firebug
					response.setCharacterEncoding("UTF-8");
					writer = response.getWriter();
					// Add secure JSON prefix
					writer.write("{}&&");
					writer.flush();
					json.serialize(writer);
				}
			} catch (Exception e) {
				throw e;
			} finally {
				if (writer != null)
					writer.close();
			}
		}
		
		
		private String buildSearchQuery(String searchWord) {
			return "SELECT * FROM Document where DocumentTitle LIKE '%" + searchWord +"%' "; 
		}
	}
