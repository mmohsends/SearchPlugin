define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/json",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-construct",
    "ecm/widget/dialog/BaseDialog",
	"ecm/model/ResultSet",
    "dijit/form/Button",
    "searchPluginDojo/Validation",
    "dojo/i18n!./nls/localization",
    "dojo/text!./templates/MostUsingDialog.html",
    "searchPluginDojo/Toaster",
    "dojox/grid/EnhancedGrid", 
    "dojo/data/ItemFileWriteStore",
    "searchPluginDojo/AdvancedFileSearchFeature",
], function (
    declare,
    lang,
    json,
    dom,
    domClass,
    domConstruct,
    BaseDialog,
    ResultSet,
    Button,
    Validation,
    lcl,
    template,
    Toaster,
	EnhancedGrid,
	ItemFileWriteStore,
	AdvancedFileSearchFeature
) {

    return declare([ BaseDialog], {
        // Inject our template in the content area of the dialog
        contentString : template,
        /**
         * Repository to use to display the tree, must be defined 
         * at instantiation time
         */
        repository: null,
        /**
         * If set to false, user can also select folder regardless
         * the filter, can be override at instantiation time
         */
        documentOnly: true,
        /**
         * An array of string. If set, only documents endings 
         * with one of these string will be displayed and selectable
         */
        filters: null,
        // We want users to be able to give parameters at instantiation time
        
        parent: null,
        _lcl: lcl,
        val: null,
        toaster: new Toaster(),
        
        show: function(_parent) {
            this.inherited(arguments);
        	this.parent = _parent;
        },
        
        hide: function() {
        	this.destroyRecursive();
        },
        
        /**
         * Create a new dialog to select a document
         * @param args Object as {repository: Repository, execute: function, (opt) documentOnly: boolean, (opt) filters: string[]}
         */
        constructor: function (args) {
            if (args) {
                lang.mixin(this, args);
            }
            // Assuming args contains a parentWidget property with mostUsingDialog
            this.mostUsingDialog = this.parentWidget.mostUsingDialog;
            this.val = new Validation();
        },

        postCreate : function () {
            var methodName = "postCreate";
            this.logEntry(methodName);
            this.inherited(arguments);
            this.setResizable(true);
            this.setSize("500","450");
            this.setTitle(this._lcl.Most_Using);
            this.generateBodyTable();
            this.logExit(methodName);
        },
     // Assuming this is inside MostUsingDialog or a similar widget
        generateBodyTable: function() {
            var data = this.getData();
            var storeData = {identifier: 'id', items: data};
            var gridStore = new dojo.data.ItemFileWriteStore({data: storeData});

            var layout = [
                {'name': 'ID', 'field': 'id'},
                {name: this._lcl.Most_Using, field: 'propertyValue', 'width': '100%'}
            ];

            var grid = new dojox.grid.EnhancedGrid({
                store: gridStore,
                structure: layout,
                rowSelector: '20px'
            });

            this.gridDiv.addChild(grid);

            // Correctly requiring AdvancedFileSearchFeature
            require(["searchPluginDojo/AdvancedFileSearchFeature"], function(AdvancedFileSearchFeature) {
                dojo.connect(grid, "onRowClick", function(evt){
                    var idx = evt.rowIndex,
                        rowData = grid.getItem(idx);
                    var propertyValue = grid.store.getValue(rowData, "propertyValue");
                    
                    require(["dojo/topic"], function(topic) {
                        topic.publish("updateTextBoxGlobalSearchValue", propertyValue);
                    });
                    
                    // Instantiate AdvancedFileSearchFeature here, assuming it is now correctly loaded
                    var advancedFileSearchFeature = new AdvancedFileSearchFeature({
                        propertyValue: propertyValue
                    });
                    console.log(propertyValue);
                    this.hide();
                }.bind(this)); // Ensure 'this' context is correct
            }.bind(this)); // Bind 'this' to ensure it refers to the correct context for 'require'

            grid.startup();
        },


        
        
        getData: function() {
        	var toaster = new Toaster();
        	params = {
 					method: "GetMostUsing"
 			};
             
        	var response = ecm.model.Request.invokeSynchronousPluginService("SearchPlugin", "AdvancedFileSearchService",
					params);	
        	var resultSet = new ResultSet(response);
        	debugger;
       
			var results = [];
			if(!resultSet.result.startsWith("ERROR")){
				results = json.parse(resultSet.result, true);
			} else {
				if (resultSet.result.includes("(ACCESS DENIED)")) {
					toaster.redToaster(lcl.ACCESS_DENIED);						
				} else {
					toaster.redToaster(lcl.FAILED_TO_FETCH_DATA);
				}
				console.log("Failed to load data!");
				console.log(resultSet);
			}
			
			this.groups = JSON.parse(JSON.stringify(results));
			
			return this.processAndReformatData(results)
        },
        
        
        
        processAndReformatData: function (entries) {
            const uniqueKeywords = new Set();
            const processedEntries = [];
            let idCounter = 1; // Initialize counter for auto-incrementing ID

            entries.forEach(entry => {
                const parts = entry.propertyValue.split(':'); // Split by ':'
                if (parts.length > 1) { // Ensure there is a part after ':'
                    const value = parts[1];
                    if (value) {
                        const cleanKeyword = value.trim().replace(/;$/, ''); // Clean the keyword
                        if (cleanKeyword && !uniqueKeywords.has(cleanKeyword)) { // Check cleanKeyword is not empty and unique
                            uniqueKeywords.add(cleanKeyword);
                            // Include the entry with the original propertyName, the unique, cleaned keyword, and an auto-increment ID
                            processedEntries.push({
                                id: idCounter++, // Use and increment the counter
                                propertyName: entry.propertyName,
                                propertyValue: cleanKeyword
                            });
                        }
                    }
                }
            });

            return processedEntries;
        },


        
    });

});