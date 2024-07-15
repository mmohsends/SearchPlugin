define([
	"dojo/_base/declare",
	"dojo/_base/lang",
	"dojo/dom-attr",
	"dojo/dom-style",
	"dojo/dom-construct",
	"idx/layout/BorderContainer",
	"dijit/layout/ContentPane",
	"dijit/form/TextBox",
	"dijit/form/Button",
	"ecm/model/Request",
	"ecm/model/ResultSet",
	"ecm/widget/layout/_LaunchBarPane",
	"ecm/widget/layout/_RepositorySelectorMixin",
	"ecm/widget/listView/ContentList",
	"ecm/widget/listView/gridModules/RowContextMenu",
	"ecm/widget/listView/modules/Toolbar",
	"ecm/widget/listView/modules/DocInfo",
	"ecm/widget/listView/gridModules/DndRowMoveCopy",
	"ecm/widget/listView/gridModules/DndFromDesktopAddDoc",
	"ecm/widget/listView/modules/Bar",
	"ecm/widget/listView/modules/ViewDetail",
	"ecm/widget/listView/modules/ViewMagazine",
	"ecm/widget/listView/modules/ViewFilmStrip",
	"ecm/widget/CheckBox",
	"searchPluginDojo/Toaster",
	"ecm/model/Desktop",
	"dojo/i18n!./nls/localization",
    "dojo/text!./templates/LinkSearchResults.html"
],

function(declare,
		lang,
		domAttr,
		domStyle,
		domConstruct,
		idxBorderContainer,
		ContentPane,
		TextBox,
		Button,
		Request,
		ResultSet,
		_LaunchBarPane,
		_RepositorySelectorMixin,
		ContentList,
		RowContextMenu,
		Toolbar,
		DocInfo,
		DndRowMoveCopy,
		DndFromDesktopAddDoc,
		Bar,
		ViewDetail,
		ViewMagazine,
		ViewFilmStrip,
		CheckBox,
		Toaster,
		Desktop,
		lcl, 
		template) {

	return declare("searchPluginDojo.LinkSearchResults", [
		_LaunchBarPane,
		_RepositorySelectorMixin
	], {
		/** @lends customSearchPluginDojo.CustomFeaturePane.prototype */
		templateString: template,
		widgetsInTemplate: true,
		toaster: new Toaster(),
		_lcl : lcl,
		
		constructor : function(args) {
			
			if (args) {
				lang.mixin(this, args);
				this.args = args;

			}
		},

		postCreate: function() {
			this.logEntry("postCreate");
			this.inherited(arguments);
			
			domAttr.set(this.searchResults.domNode, "role", "region");
			domAttr.set(this.searchResults.domNode, "aria-label", this.messages.browse_content_list_label);
			this.searchResults.setContentListModules(this.getContentListModules());
			this.searchResults.setGridExtensionModules(this.getContentListGridModules());		
			this.setRepositoryTypes("p8");
			this.createRepositorySelector();
			this.doRepositorySelectorConnections();
			this.loadContent()
			// If there is more than one repository in the list, show the selector to the user.
			if (this.repositorySelector.getNumRepositories() > 1) {
				domConstruct.place(this.repositorySelector.domNode, this.repositorySelectorArea, "only");
			}
			
			this.runSearch();
			  // Attach the button click event
            this.viewButton.on("click", lang.hitch(this, this.viewSelectedItem));
			this.logExit("postCreate");
		},
		
		/**
		 * Sets the repository being used for search.
		 * 
		 * @param repository
		 * 			An instance of {@link ecm.model.Repository}
		 */
		setRepository: function(repository) {
			this.repository = repository;
			if (this.repositorySelector && this.repository) {
				this.repositorySelector.getDropdown().set("value", this.repository.id);
			}
			this.clear();
		},
		
		/**
		 * Returns the content list grid modules used by this view.
		 * 
		 * @return Array of grid modules.
		 */
		getContentListGridModules: function() {
			var array = [];
			array.push(DndRowMoveCopy);
			array.push(DndFromDesktopAddDoc);
			array.push(RowContextMenu);
			return array;
		},

		/**
		 * Returns the content list modules used by this view.
		 * 
		 * @return Array of content list modules.
		 */
		getContentListModules: function() {
			var viewModules = [];
			viewModules.push(ViewDetail);
			viewModules.push(ViewMagazine);
			if (ecm.model.desktop.showViewFilmstrip) {
				viewModules.push(ViewFilmStrip);
			}
			var array = [];
			array.push(DocInfo);
			console.log("array", array)
			return array;
		},

		/**
		 * Loads the content of the pane. This is a required method to insert a pane into the LaunchBarContainer.
		 */
		loadContent: function() {
			this.logEntry("loadContent");
			if (!this.repository) {
				this.setPaneDefaultLayoutRepository();
			} else if (!this.isLoaded && this.repository && this.repository.connected) {
				this.setRepository(this.repository);
				this.isLoaded = true;
				this.needReset = false;
			}	
			this.logExit("loadContent");
		},

		/**
		 * Resets the content of this pane.
		 */
		reset: function() {
			this.logEntry("reset");
			
			if (this.repositorySelector && this.repository)
				this.repositorySelector.getDropdown().set("value", this.repository.id);
			this.needReset = false;
			
			this.logExit("reset");
		},
		
		/**
		 * Runs the search entered by the user.
		 */
		runSearch: function() {
			var requestParams = {};
			requestParams.repositoryId = this.repository.id;
			requestParams.repositoryType = this.repository.type;
			requestParams.searchWord = this.args.searchKey;
			requestParams.searchProperties = {};
			Request.invokePluginService("SearchPlugin", "LinkSearchService",
				{
					requestParams: requestParams,
					requestCompleteCallback: lang.hitch(this, function(response) {	// success
						response.repository = this.repository;
						var resultSet = new ResultSet(response);
						this.searchResults.setResultSet(resultSet);
					})
				}
			);
		},

		/**
		 * Clears the search results.
		 */
		clear: function() {

			this.searchResults.reset();
		},

		viewSelectedItem: function(){
	        	var toaster = new Toaster();
	        	var _this = this;
	        	var files = this.searchResults.getSelectedItems();
	        	if (files.length == 0) {
	        		toaster.redToaster(lcl.PLEASE_SELECT_ONE_DOC);
	        		return;
	        	} else if (files.length > 1) {
	        		toaster.redToaster(lcl.ONLY_ONE_FILE_CAN_BE_SELECTED_AT_A_TIME);
	        		return;
	        	}
	        	 var selectedItem = files[0];
	        	 var selectedItemDocId =selectedItem.attributeDisplayValues.ID;
	        	params = {};
	        	var url = "bookmark.jsp?desktop="+Desktop.id+"&repositoryId="+Desktop.defaultRepositoryId+"&docid="+selectedItemDocId;
				url = ecm.model.Request.appendSecurityToken(url);	
				OpenWindowWithPostNewWindow(url, "width=1000, height=600, left=100, top=100, resizable=no, scrollbars=no", "ViewFileContent", params);
	        }
	      

	    });

	});

	function OpenWindowWithPostNewWindow (url, windowoption, name, params) {
		 var form = document.createElement("form");
		 form.setAttribute("method", "post");
		 form.setAttribute("action", url);
		 form.setAttribute("target", name);
		 for (var i in params) {
		   if (params.hasOwnProperty(i)) {
		     var input = document.createElement('input');
		     input.type = 'hidden';
		     input.name = i;
		     input.value = params[i];
		     form.appendChild(input);
		   }
		 }
		 document.body.appendChild(form);
		 //note I am using a post.htm page since I did not want to make double request to the page
		 //it might have some Page_Load call which might screw things up.
		 var printWindow = window.open("", name, windowoption);
		 form.submit();
		 document.body.removeChild(form);
		 
	}