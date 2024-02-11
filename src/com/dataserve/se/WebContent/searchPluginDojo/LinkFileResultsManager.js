define(
	[
        "dojo/_base/declare",
        "dojo/_base/lang",
        "dojo/dom",
        "dojo/dom-class",
        "dojo/dom-construct",
        "dojo/dom-style",
        "dojo/data/ItemFileWriteStore",
        "dojox/grid/EnhancedGrid", 
    	"dojox/grid/enhanced/plugins/IndirectSelection",
    	"ecm/model/ResultSet",
    	"ecm/model/User",
    	"ecm/model/Desktop",
        "dijit/_WidgetBase",
        "dijit/_TemplatedMixin",
        "dijit/_WidgetsInTemplateMixin",
        "dijit/layout/ContentPane",
        "dijit/form/Button",
    	"dojo/json",
    	"dojo/i18n!./nls/localization",
        
        "dojo/text!./templates/LinkFileResultsManager.html",
        "searchPluginDojo/Toaster"
    ], 
    function (
        declare,
        lang,
        dom,
        domClass,
        domConstruct,
        style,
        ItemFileWriteStore,
        EnhancedGrid,
        IndirectSelection,
        ResultSet,
        User,
        Desktop,
        _WidgetBase, 
        _TemplatedMixin,
        _WidgetsInTemplateMixin,
        ContentPane,
        Button,
        json,
        lcl,
        template,
        Toaster
    ) {
	
        return declare([_WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin, ContentPane], {
        	templateString: template,
        	parent: null,
        	_lcl: lcl,
        	
            constructor : function (args) {
                if (args) {
                    lang.mixin(this, args);
                }
            },

            postCreate : function () {
                this.inherited(arguments);
//                if (!permissions["FILE_DESTROY_REPORT"] || permissions["FILE_DESTROY_REPORT"].length == 0) {
//                	return;
//                }
                
                this.headerContainer = new ContentPane({style: "width: 100%; height: 10%;", region: "top"});
                this.addChild(this.headerContainer);
                
                this.bodyContainer = new ContentPane({style: "width: 100%; height: 90%;", region: "center"});
                this.addChild(this.bodyContainer);
                
            	this.generateBody();
//                this.generateHeader();
            	this.bodyContainer.resize();
            },
            
           
           
            generateBody: function() {
            	var data = this.getData();
                var storeData = {identifier: 'id', items: data};
    		    var gridStore = new dojo.data.ItemFileWriteStore({data: storeData});
     	            
 	            var layout = [
 	                  {name: 'ID', field: 'id', width: '10px', hidden: true},
 	                  {name: 'destroyStatusId', field: 'destroyStatusId', width: '10px', hidden: true},
 	                  {name: lcl.CLASS_NAME, field: 'className', width: '15%'},
	    		      {name: lcl.DOCUMENT_TITLE, field: 'documentTitle', width: '20%'},
 	    		      {name: lcl.NO_PAGES, field: 'noPages', width: '10%'},
 	    		      {name: lcl.SAVE_PERIOD, field: 'tempSavePeriodAllDeptSum', width: '10%'},
 	    		      {name: lcl.FILE_CREATE_HIJRI_YEAR, field: 'createYear', width: '10%'},
 	    		      {name: lcl.FOLDER_NO, field: 'folderNo', width: '10%'},
 	    		      {name: lcl.BOX_NO, field: 'boxNo', width: '10%'}, 
 	    		      {name: lcl.FILE_DESTROY_STATUS, field: 'destroyStatus', width: '15%'},    		     
 	    		      ];
     	            
                this.destroyFileReportGrid = new dojox.grid.EnhancedGrid({
			        store: gridStore,
			        structure: layout,
			        style: "width: 100%; height: 100%;",
			        rowSelector: '0px',
			        plugins: {indirectSelection: {headerSelector:true, width:"20px", styles:"text-align: center;"}}
		        });

     	        this.bodyContainer.addChild(this.destroyFileReportGrid);
     	        this.destroyFileReportGrid.startup();
            },
            
            getData: function() {
            	var toaster = new Toaster();
            	params = {
     					method: "GetMostUsing",
     					userId: Desktop.userId
     			};
                 
     			var response = ecm.model.Request.invokeSynchronousPluginService("SearchPlugin", "AdvancedFileSearchService",params);
     			var resultSet = new ResultSet(response);
     			debugger;
	 			message = new ecm.widget.dialog.MessageDialog();
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
    			return results;
            },
               
         
        });

    });

