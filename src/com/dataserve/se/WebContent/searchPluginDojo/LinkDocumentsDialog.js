define([ "dojo/_base/declare", 
		"dojo/_base/lang", 
		"dojo/html", 
		"dojo/dom",
		"dojo/dom-class", 
		"dojo/dom-construct", 
		"ecm/widget/dialog/BaseDialog",
		"ecm/model/ResultSet",
		"dojo/json", 
		"dijit/form/Button", 
		"dijit/form/FilteringSelect",
		"dijit/form/TextBox",
		"dijit/form/Textarea",
		"dijit/form/RadioButton", 
		"dijit/Dialog", 
		"dijit/form/NumberTextBox",
		"dijit/form/ComboBox", 
		"dojo/_base/array", 
		"dojox/grid/cells/dijit",
        "dijit/layout/ContentPane", 
		"dijit/_WidgetsInTemplateMixin",
		"dojo/i18n!./nls/localization",
		"dojo/text!./templates/LinkDocuments.html",
		"searchPluginDojo/Toaster",
        "dojox/grid/EnhancedGrid", 
        "dojo/data/ItemFileWriteStore",
        "searchPluginDojo/LinkSearchResults",


		], 
		function(declare,
		lang, 
		html, 
		dom, 
		domClass, 
		domConstruct, 
		BaseDialog,
		ResultSet,
		json, 
		Button, 
		FilteringSelect, 
		TextBox, 
		Textarea, 
		RadioButton, 
		Dialog, 
		NumberTextBox,
		ComboBox, 
		array, 
		cells,
		ContentPane,
		_WidgetsInTemplateMixin, 
		lcl, 
		template, 
		Toaster,
		EnhancedGrid,
		ItemFileWriteStore,
		LinkSearchResults
		) {

	return declare([ BaseDialog, _WidgetsInTemplateMixin ], {
		// Inject our template in the content area of the dialog
		contentString : template,
		
		// Set to true if widget template contains DOJO widgets.
		widgetsInTemplate: true,
		
		/**
		 * Repository to use to display the tree, must be defined 
		 * at instantiation time
		 */
		repository : null,
		/**
		 * If set to false, user can also select folder regardless
		 * the filter, can be override at instantiation time
		 */
		documentOnly : true,
		/**
		 * An array of string. If set, only documents endings 
		 * with one of these string will be displayed and selectable
		 */
		filters : null,
		// We want users to be able to give parameters at instantiation time

		parent : null,
		data : null,
		_lcl : lcl,
		toaster: new Toaster(),

		args: null,
		classgrid:null,
		searchKey:null,
	
		searchResultObj:null,
		grid:null,

		show : function(_parent) {
			this.inherited(arguments);
			this.parent = _parent;
			
		},

		hide : function() {
			this.destroyRecursive();
		},

		/**
		 * Create a new dialog to select a document
		 * @param args Object as {repository: Repository, execute: function, (opt) documentOnly: boolean, (opt) filters: string[]}
		 */
		constructor : function(args) {
			
			if (args) {
				lang.mixin(this, args);
				this.args = args;

			}
		},

		postCreate : function() {
			var methodName = "postCreate";
			this.logEntry(methodName);
			this.inherited(arguments);
			this.setResizable(true);
			this.setTitle(lcl.Link_Documents);
			this.LinkAndDeleteBtn();
			this.LinkedDocument();			
			this.logExit(methodName);
		},
		



		
		LinkAndDeleteBtn: function(){
			var linkLabel = this._lcl.LINK;
			this.linkBtn = new Button({
				label : linkLabel,
				onClick : lang.hitch(this, function() {
					this.addLinkDocument();
					this.grid.destroy();
					this.LinkedDocument();

				}),
			});
			
			var deleteLabel = this._lcl.DELETE;
			this.removeBtn = new Button({
				label : deleteLabel,
				onClick : lang.hitch(this, function() {
					this.deleteLinkDocument()
					this.grid.destroy();
					this.LinkedDocument();

				}),
				
			});
			
  		    this.gridBtn.addChild(this.linkBtn);
  		    this.gridBtn.addChild(this.removeBtn);

			
		},
      
		
		LinkedDocument: function() {
		    var data = {
		        identifier: 'documentId',
		        items: this.getLikedDocument()
		    };

		    var store = new dojo.data.ItemFileWriteStore({ data: data });

		    var layout = [
		        { name: this._lcl.DOC_NAME, field: 'documentName', width: '33.3%' },
		        { name: this._lcl.DOC_CLASS, field: 'documentClass', width: '33.3%' },
		        { name: this._lcl.CREATED_BY, field: 'createdBy', width: '33.3%' },
		    ];

		    this.grid = new dojox.grid.EnhancedGrid({
		        store: store,
		        structure: layout,
		        style: 'width: 100%; height: 100%;',
		        rowSelector: '20px',
		        plugins: {
		            indirectSelection: {
		                headerSelector: true,
		                width: '40px',
		                styles: 'text-align: center;'
		            }
		        }
		    });

		    this.gridDiv.addChild(this.grid);

		    this.grid.startup();
		},
	  
	  
	  openSearchResult : function(){
		this.clearViewSearch();
		var params = {};
		params.searchKey = this.searchInput.get('value');
		_this=this;
		params.parent=_this;
		var linkSearchResults = new LinkSearchResults(params);
		this.viewContainer.addChild(linkSearchResults);
		this.viewContainer.resize();
		this.searchResultObj = linkSearchResults
		return linkSearchResults

	  },
	  
	  addLinkDocument: function(){
		  var docs = this.searchResultObj.searchResults.getSelectedItems();
		  if(docs.length == 0){
				this.toaster.redToaster(lcl.PLEASE_SELECT_ONE_DOC);
			}else{
		  var docInfo = docs.map(function(item){
			  	return item.attributeDisplayValues.ID;
		  });
		  }
		  console.log("docInfo: ",docInfo)
		  console.log("docInfo: ",JSON.stringify(docInfo))
      	params = {
				method: "AddLinkDocument",
          		"docInfo": JSON.stringify(docInfo),
				"mainDocId": this.args.docGuid,

	
			};
             
 			var response = ecm.model.Request.invokeSynchronousPluginService("SearchPlugin", "AdvancedFileSearchService", params);
 			var resultSet = new ResultSet(response);
 			if(!resultSet.result.startsWith("ERROR")){
 				this.toaster.greenToaster(this._lcl.OBJ_CREATED_DOC.replace("XX", this._lcl.Link_Documents));
 			} else {
 				if (resultSet.result.includes("(ACCESS DENIED)")) {
 					this.toaster.redToaster(lcl.ACCESS_DENIED);						
				} else {
					if (resultSet.result == "ERROR: Symbolic Name already in use") {
						this.toaster.redToaster(lcl.SYMBOLIC_NAME_IN_USE);
					} else {
						this.toaster.redToaster(lcl.SAVE_FAILED);
					}
				}
 				console.log("User creation failed!");
 				console.log(resultSet);
 			}

			        
  },
  
  		getLikedDocument: function(){
  			var toaster = new Toaster();
			var mainDocId = this.args.docGuid
  			params = {
				method: "GetLinkDocument",
          		"mainDocId":mainDocId,	
  			};
  			
 			var response = ecm.model.Request.invokeSynchronousPluginService("SearchPlugin", "AdvancedFileSearchService",params);
 			var resultSet = new ResultSet(response);
       
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
			return results;
  		},
  		
  		deleteLinkDocument: function(){
  			var item = []
			var gridSelect = this.grid.selection.getSelected()
			if(this.grid.selection.getSelected().length == 0){
				this.toaster.redToaster(lcl.PLEASE_SELECT_ONE_DOC);
				return "";
			}else{
				for (let i=0;i<gridSelect.length;i++){
				item[i]={"mainDocId":gridSelect[i].mainDocId.toString(),"documentId":gridSelect[i].documentId.toString()}
				}
				}

  	      	params = {
  					method: "DeleteLinkDocument",
  	          		"gridData": JSON.stringify(item),
	
  				};
  	             
  	 			var response = ecm.model.Request.invokeSynchronousPluginService("SearchPlugin", "AdvancedFileSearchService", params);
  	 			var resultSet = new ResultSet(response);
  	 			if(!resultSet.result.startsWith("ERROR")){
  	 				this.toaster.greenToaster(this._lcl.OBJ_DELETED_DOC.replace("XX", this._lcl.Link_Documents));
  	 			} else {
  	 				if (resultSet.result.includes("(ACCESS DENIED)")) {
  	 					this.toaster.redToaster(lcl.ACCESS_DENIED);						
  					} else {
  						if (resultSet.result == "ERROR: Symbolic Name already in use") {
  							this.toaster.redToaster(lcl.SYMBOLIC_NAME_IN_USE);
  						} else {
  							this.toaster.redToaster(lcl.SAVE_FAILED);
  						}
  					}
  	 				console.log("User creation failed!");
  	 				console.log(resultSet);
  	 			}

  				        
  	  },
	  

		
		clearViewSearch: function() {
			if (this.viewContainer.getChildren() && this.viewContainer.getChildren().length > 0) {
				this.viewContainer.getChildren().forEach(function(child){
					child.destroyRecursive();
				});
			}
		},

			
			

		
    
	});

});