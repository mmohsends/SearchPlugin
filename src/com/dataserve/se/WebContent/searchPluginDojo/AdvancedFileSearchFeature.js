define([
	"dojo/_base/declare",
	    "dijit/_WidgetBase",
	    "dijit/_TemplatedMixin",
	    "dijit/_WidgetsInTemplateMixin",
	"ecm/widget/layout/_LaunchBarPane",
	"dijit/form/Button",
	"dijit/layout/ContentPane",
	"searchPluginDojo/AdvancedFileSearchManager",
    "searchPluginDojo/MostUsingDialog",
	"searchPluginDojo/LinkFileResultsManager",
	
    "searchPluginDojo/AdvancedFileSearchResults",
    "helpPluginDojo/HelpDialog",
    "searchPluginDojo/Toaster",
	"dojo/i18n!./nls/localization",
	"dojo/text!./templates/AdvancedFileSearchFeature.html"
],
function(declare,
		_WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin,
		_LaunchBarPane,
		Button,
		ContentPane,
		AdvancedFileSearchManager,
		MostUsingDialog,
		LinkFileResultsManager,
		AdvancedFileSearchResults,
		HelpDialog,
		Toaster,
		lcl,
		template) {
	/**
	 * @name eDSAdminPluginDojo.EDSHomeFeature
	 * @class 
	 * @augments ecm.widget.layout._LaunchBarPane
	 */
	return declare("searchPluginDojo.AdvancedFileSearchFeature", [
		_LaunchBarPane,_WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin
	], {
		/** @lends eDSAdminPluginDojo.EDSHomeFeature.prototype */
		_lcl: lcl,
		templateString: template,
		toaster: new Toaster(),
		
		// Set to true if widget template contains DOJO widgets.
		widgetsInTemplate: false,
		propertyValue: null, // Default value or null
        constructor: function(params) {
            // If params are provided and contain propertyValue, set it
            if (params && params.hasOwnProperty('propertyValue')) {
                this.propertyValue = params.propertyValue;
            }
            // Initialize other properties or setup tasks here
        },

		postCreate: function() {
			
			this.logEntry("postCreate");
			this.inherited(arguments);
			  
			  require(["dojo/topic"], function(topic) {
			        topic.subscribe("updateTextBoxGlobalSearchValue", function(value){
			            if (this.searchInput) {
			                this.searchInput.set('value', value);
			            }
			        }.bind(this));
			    }.bind(this));
			/**
			 * Add custom logic (if any) that should be necessary after the feature pane is created. For example,
			 * you might need to connect events to trigger the pane to update based on specific user actions.
			 */
			
			this.logExit("postCreate");
		},
		
		
		/**
		 * Optional method that sets additional parameters when the user clicks on the launch button associated with 
		 * this feature.
		 */
		setParams: function(params) {
			this.logEntry("setParams", params);
			
			if (params) {
				
				if (!this.isLoaded && this.selected) {
					this.loadContent();
				}
			}
			
			this.logExit("setParams");
		},

		/**
		 * Loads the content of the pane. This is a required method to insert a pane into the LaunchBarContainer.
		 */
		loadContent: function() {
			this.logEntry("loadContent");
			

			if (!this.isLoaded) {
				/**
				 * Add custom load logic here. The LaunchBarContainer widget will call this method when the user
				 * clicks on the launch button associated with this feature.
				 */
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
			
			/**
			 * This is an option method that allows you to force the LaunchBarContainer to reset when the user
			 * clicks on the launch button associated with this feature.
			 */
			this.needReset = false;
			
			this.logExit("reset");
		},
		
		openAdvancedFileSearchManager :function(){
			
			this.clearView();
			var params = {};
			_this=this;
			params.parent=_this;
			var advancedFileSearchManager = new AdvancedFileSearchManager(params);
			this.viewContainer.addChild(advancedFileSearchManager);
			this.viewContainer.resize();
		},
			
		
		generalSearch :function(){
			var searchValue = this.searchInput.value;
            if (typeof searchValue === 'string' && searchValue.trim().length === 0) {
            	this.toaster.redToaster(lcl.SEARCH_WORD_IS_REQUIRED);
        		return;
    		} 
            
			this.clearView();
			var params = {};
			_this=this;
			params.operation = 'generalSearch';
			//params.classSymbolicName='workwork';
			//params.searchProperties={};

			params.searchWord = this.searchInput.value;
			params.parent=_this;
			var advancedFileSearchResults = new AdvancedFileSearchResults(params);
			this.viewContainer.addChild(advancedFileSearchResults);
			this.viewContainer.resize();;
		},
	
		
		openMostUsingDialog: function() {
		    // Store the dialog instance in a property of 'this'
		    this.mostUsingDialog = new MostUsingDialog({
		        fitContentArea : true, 
		        lockFullscreen: false,
		        parentWidget: this,
		    });
		    this.mostUsingDialog.show(this);
		},
		openOnlineHelp : function(){
			
			var _this = this;
			params = {};
			params.fitContentArea = true;
			params.lockFullscreen= false;
			params.parent = _this;
			params.pageTitle = lcl.ADVANCED_SEARCH_FILE;
			params.pdfFileName = "182_183_185_186";
			var helpDialog = new HelpDialog(params);
			helpDialog.show(_this);
		},

		
		/**
		 * clear home container when click to menu 
		 */
		clearView: function() {
			if (this.viewContainer.getChildren() && this.viewContainer.getChildren().length > 0) {
				this.viewContainer.getChildren().forEach(function(child){
					child.destroyRecursive();
				});
			}
		},
		

	});
});

