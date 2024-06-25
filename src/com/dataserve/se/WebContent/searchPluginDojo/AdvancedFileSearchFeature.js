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
	 "ecm/model/ResultSet",
	
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
		ResultSet,
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
		
		generalSearch: function() {
		    var searchValue = this.searchInput.value;
		    if (typeof searchValue === 'string' && searchValue.trim().length === 0) {
		        this.toaster.redToaster(lcl.SEARCH_WORD_IS_REQUIRED);
		        return;
		    }
		    
		    // Check for SQL injection patterns, including a wide range of SQL keywords
		    var sqlInjectionPattern = /\b(SELECT|INSERT|UPDATE|DELETE|DROP|ALTER|CREATE|EXEC|UNION|FROM|WHERE|JOIN|INNER|OUTER|LEFT|RIGHT|FULL|CROSS|NATURAL|GROUP BY|ORDER BY|HAVING|LIMIT|OFFSET|FETCH|ROW|ROWS|ONLY|DISTINCT|AS|INTO|VALUES|SET|ON|ALL|ANY|SOME|NOT|EXISTS|IN|LIKE|AND|OR|XOR|IS|NULL|NOT NULL|TRUE|FALSE|WITH|TABLE|DATABASE|SCHEMA|TRUNCATE|COMMENT|REVOKE|GRANT|PRIVILEGES|REFERENCES|CASCADE|RESTRICT|NO ACTION|SET DEFAULT|SET NULL|AFTER|BEFORE|TRIGGER|PROCEDURE|FUNCTION|INDEX|VIEW|CURSOR|LOCK|KEY|PRIMARY|FOREIGN|CONSTRAINT|CHECK|DEFAULT|IF|ELSE|ENDIF|CASE|WHEN|THEN|END|BEGIN|DECLARE|FETCH|WHILE|LOOP|REPEAT|UNTIL|DO|EXIT|GOTO|BREAK|CONTINUE|EXECUTE|PREPARE|DEALLOCATE|DESCRIBE|EXPLAIN|ANALYZE|SHOW|DESCRIBE|USE|ALTER|CALL|HANDLER|ITERATE|LEAVE|OPEN|CLOSE|DUMP|KILL|LOAD|RESET|PURGE|OPTIMIZE|ANALYZE|REPAIR|BACKUP|RESTORE|FLUSH|RESET|SHUTDOWN|START|STOP|SLAVE|MASTER|SYNC|DESCRIBE|EXPLAIN|ANALYZE)\b|(--|\/\*|\*\/|;|')/gi;
		    var invalidParts = searchValue.match(sqlInjectionPattern);
		    
		    // Debugging: Log the input and the result of the match
		    
		    if (invalidParts && invalidParts.length > 0) {
		        var invalidWords = invalidParts.join(', ');
		        this.toaster.redToaster(lcl.INPUT_CONTENT_SEARCH_INVAILD + invalidWords);
		        return; // Return the invalid search words
		    }
		    
		    // Sanitize the input by escaping special characters (if necessary)
		    searchValue = searchValue.replace(/'/g, "''");

		    this.clearView();
		    var params = {};
		    var _this = this;
		    params.operation = 'generalSearch';
		    searchValue = this.EncryptKeyWord(searchValue)
		    params.searchWord = searchValue;
		    params.parent = _this;
		    var advancedFileSearchResults = new AdvancedFileSearchResults(params);
		    this.viewContainer.addChild(advancedFileSearchResults);
		    this.viewContainer.resize();
		},

		
		EncryptKeyWord: function(keywordSearch){
			
	      	params = {
					method: "EncryptionUtilCommand",
					"keywordSearch": keywordSearch,
				};
	             
	 			var response = ecm.model.Request.invokeSynchronousPluginService("SearchPlugin", "AdvancedFileSearchService", params);
	 			var resultSet = new ResultSet(response);
	 			if(!resultSet.result.startsWith("ERROR")){
	 				return resultSet.result ;
	 			} else {
	 				if (resultSet.result.includes("(ACCESS DENIED)")) {
	 					this.toaster.redToaster(lcl.ACCESS_DENIED);						
					} else {
							this.toaster.redToaster(lcl.UNAVAILABLE);
						}
					}
	 			return resultSet.result ;
	 				console.log(resultSet);
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

