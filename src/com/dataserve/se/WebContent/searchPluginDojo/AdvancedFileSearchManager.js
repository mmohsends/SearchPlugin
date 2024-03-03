define([
    "dojo/_base/declare",
    "ecm/widget/layout/_LaunchBarPane",
    "dojo/_base/lang",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-construct",
    "dojo/dom-style",
    "dijit/_WidgetBase",
    "dojox/grid/EnhancedGrid",
    "ecm/model/ResultSet",
    "dojox/grid/enhanced/plugins/IndirectSelection",
    "dojo/data/ItemFileWriteStore",
    "dojo/json",
    "dijit/form/Button",
    "dojo/store/Memory",
    "dijit/form/FilteringSelect",
    "dijit/form/Select",
    "dijit/form/TextBox",
    "dijit/Dialog",
    "dijit/form/NumberTextBox",
    "dijit/form/Select",
    "dojo/_base/array",
    "dijit/tree/ObjectStoreModel",
    "dijit/Tree",
    "dijit/popup",
    "dojox/grid/LazyTreeGrid",
    "dijit/tree/ForestStoreModel",
    "dojo/store/Observable",
    "dijit/form/ComboBox",
    "dijit/form/DropDownButton",
    "dijit/TooltipDialog",
    "dijit/_TemplatedMixin",
    "dijit/layout/ContentPane",
    "dojo/i18n!./nls/localization",
    "dojo/text!./templates/AdvancedFileSearchManager.html",
    "searchPluginDojo/AdvancedFileSearchResults",
    "searchPluginDojo/Toaster",
    "ecm/model/Desktop",
    "dojo/ready",
    "dojo/domReady!"
], function (
    declare,
    _LaunchBarPane,
    lang,
    dom,
    domClass,
    domConstruct,
    style,
    _WidgetBase,
    EnhancedGrid,
    ResultSet,
    IndirectSelection,
    ItemFileWriteStore,
    json,
    Button,
    Memory,
    FilteringSelect,
    Select,
    TextBox,
    Dialog,
    NumberTextBox,
    Select,
    array,
    ObjectStoreModel,
    Tree,
    popup,
    LazyTreeGrid,
    ForestStoreModel,
    Observable,
    ComboBox,
    DropDownButton,
    TooltipDialog,
    _TemplatedMixin,
    ContentPane,
    lcl,
    template,
    AdvancedFileSearchResults,
    Toaster,
    Desktop,
    ready
) {
	 var style = document.createElement('style');
	    style.type = 'text/css';
	    style.innerHTML = '.dijitSelect .dijitMenuItem { padding-left: 4px; text-align: right; direction: rtl; }';
	    document.getElementsByTagName('head')[0].appendChild(style);
    return declare("searchPluginDojo.AdvancedFileSearchManager", [_LaunchBarPane], {

        templateString: template,
        parent: null,
        _lcl: lcl,
        toaster: new Toaster(),
        classSelect: null,
        classPropertyGrid: null,
        classgrid: null,
        _parent: null,
        classes: null,

        constructor: function (args) {
            _parent = args.parent;
        },

        postCreate: function () {
            this.inherited(arguments);
            var that = this;

            ready(function () {
                that.populateClassificationsDropdown();
                // Other initialization code that relies on DOM elements can be placed here
            });
        },
 
        populateClassificationsDropdown: function () {
            var that = this;
            this.selectedItem = null; // Variable to store the selected item

            var data = that.getDialogData();
            var formattedData = this._formatDataForTree(data);

            var memStore = new Memory({
                data: formattedData,
                getChildren: function (object) {
                    return this.query({ parent: object.id });
                }
            });

            var myModel = new ObjectStoreModel({
                store: memStore,
                query: { id: 'root' }
            });

            this.tree = new Tree({
                model: myModel,
                onClick: function (item) {
                    that.selectedItem = item;
                    var buttonLabel = that.dropDownButton.domNode.querySelector('.dijitButtonNode');
                    if (buttonLabel) {
                        buttonLabel.textContent = item.name; // Update the text content of the label
                    }
                }
            });

            this.addButton = new Button({
                label:this._lcl.SELECT,
                onClick: function () {
                    if (that.selectedItem) {
                        console.log("Selected Item:", that.selectedItem);
                        var classSymbolicName =that.selectedItem.id;
                        that.getClassCustomProperty(classSymbolicName);
                    }
                    dijit.popup.close(that.tooltipDialog);
                }
            });
           

            this.cancelButton = new Button({
                label:this._lcl.CANCEL,
                onClick: function () {
                    dijit.popup.close(that.tooltipDialog);
                }
            });
           

            // Create a scrollable container for the tree
            var treeScrollContainer = new ContentPane({
                style: "height: 100%; overflow: auto;",
                content: this.tree
            });

            this.tooltipDialog = new TooltipDialog({
                style: "width: 500px;height: 400px" // Adjust width as needed
            });

            // Append the widgets to the TooltipDialog's container node
            treeScrollContainer.placeAt(this.tooltipDialog.containerNode);
            this.addButton.placeAt(this.tooltipDialog.containerNode);
            this.cancelButton.placeAt(this.tooltipDialog.containerNode);


            this.dropDownButton = new DropDownButton({
                label: this._lcl.Select_CLASSS,
                dropDown: this.tooltipDialog
            });
            
            this.dropDownButton.startup();
            var buttonLabel = that.dropDownButton.domNode.querySelector('.dijitButtonNode');
            if (buttonLabel) {
                buttonLabel.textContent = this._lcl.Select_CLASSS; // Update the text content of the label
            }
            var classSelectorDiv = this.ClassSelectorDiv;
            if (classSelectorDiv) {
                this.dropDownButton.placeAt(classSelectorDiv);
                this.tree.startup();
                this.addButton.startup();
                this.cancelButton.startup();
                this.tooltipDialog.startup();
                
            } else {
                console.error("Element 'ClassSelectorDiv' not found in the DOM.");
            }
        },


        
        _formatDataForTree: function (originalData) {
            var formattedData = [];

            function processItem(item, parentId) {
                formattedData.push({
                    id: item.sympolicName, // Unique identifier
                    name:ecm.model.desktop.valueFormatter.locale === 'en' ? item.nameEn : item.nameAr , // Label for tree item
                    parent: parentId // Parent's unique identifier
                });

                if (item.children && item.children.length > 0) {
                    item.children.forEach(function (child) {
                        processItem(child, item.sympolicName);
                    });
                }
            }

            // Add a root item if your data does not have one
            formattedData.push({ id: 'root', name: this._lcl.Select_CLASSS, parent: null });

            originalData.forEach(function (item) {
                processItem(item, 'root');
            });

            return formattedData;
        },





		getDialogData : function() {
			params = {
				method : "GetClassificationsByUser",
				userId: ecm.model.desktop.userId
			};

			var response = ecm.model.Request.invokeSynchronousPluginService("SearchPlugin", "AdvancedFileSearchService",
					params);
			var resultSet = new ResultSet(response);

			var results = [];
			if (!resultSet.result.startsWith("ERROR")) {
				results = this.fullStructure = json.parse(resultSet.result, true);
			} else {
				if (resultSet.result.includes("(ACCESS DENIED)")) {
					this.toaster.redToaster(lcl.ACCESS_DENIED);						
				} else {
					this.toaster.redToaster(lcl.FAILED_TO_FETCH_DATA);
				}
				console.log("Failed to load data!");
				console.log(resultSet);
			}
			
			this.classes = JSON.parse(JSON.stringify(results));

			return results;
		},
		
//
//		getClassCustomProperty: function(classSymbolicName) {
//			var that = this;
//		    var data = this.getClassCustomPropertyData(classSymbolicName);
//
//		    var container = document.getElementById('ProertyDataGrideDiv');
//		    if (!container) {
//		        console.error('ProertyDataGrideDiv is not a valid DOM element');
//		        return;
//		    }
//
//		    // Clear existing content
//		    container.innerHTML = '';
//
//		    // Create the fixed Document Title field
//		    var titleContainer = document.createElement('div');
//		    titleContainer.style.marginBottom = '10px';
//		    
//		    var titleLabel = document.createElement('label');
//		    titleLabel.textContent = this._lcl.DOC_TITLE;
//		    titleLabel.style.marginRight = '5px';
//		    titleContainer.appendChild(titleLabel);
//		    
//		    var titleInput = document.createElement('input');
//		    titleInput.type = 'text';
//		    titleInput.setAttribute('data-symbolic-name', 'DocumentTitle');
//		    titleInput.setAttribute('data-symbolic-name-dataType', "String");
//		    titleInput.style.padding = '5px';
//		    titleInput.style.border = '1px solid #ccc';
//		    titleInput.style.borderRadius = '4px';
//		    titleInput.style.width = 'calc(100% - 60px)';
//		    titleContainer.appendChild(titleInput);
//
//		    container.appendChild(titleContainer);
//
//		    // Create a row to hold pairs of elements
//		    var rowDiv = document.createElement('div');
//		    rowDiv.style.display = 'flex';
//		    rowDiv.style.justifyContent = 'space-between';
//		    rowDiv.style.flexWrap = 'wrap';
//
//		    data.forEach(function(item, index) {
//		        var dataType = item.dataType;
//		        var symbolicName = item.symbolicName;  // Define symbolicName here
//		        var choiceList = item.isChoiceList;
//
//		        
//
//		        var inputContainer = document.createElement('div');
//		        inputContainer.style.flex = '1 1 45%';
//		        inputContainer.style.marginBottom = '10px';
//		        
//		        var label = document.createElement('label');
//		       // label.textContent = symbolicName + ' (' + dataType + '): ';
//		        label.textContent = item.displayName;
//		        label.style.marginRight = '5px';
//		        inputContainer.appendChild(label);
//
//		        var element;
//		        if (["String", "Integer", "Float"].includes(dataType)) {
//		            element = document.createElement('input');
//		            element.type = 'text';
//		        } else if (dataType === "Date") {
//		            element = document.createElement('input');
//		            element.type = 'date';
//		        }
//
//		        if (element) {
//		            element.setAttribute('data-symbolic-name', symbolicName);
//		            element.setAttribute('data-symbolic-name-dataType', dataType);
//		            element.style.padding = '5px';
//		            element.style.border = '1px solid #ccc';
//		            element.style.borderRadius = '4px';
//		            element.style.width = 'calc(100% - 60px)';
//		            inputContainer.appendChild(element);
//		        }
//
//		        rowDiv.appendChild(inputContainer);
//
//		        if (index % 2 === 1 || index === data.length - 1) {
//		            container.appendChild(rowDiv);
//		            rowDiv = document.createElement('div');
//		            rowDiv.style.display = 'flex';
//		            rowDiv.style.justifyContent = 'space-between';
//		            rowDiv.style.flexWrap = 'wrap';
//		        }
//		    });
//
//		    var submitButton = document.createElement('button');
//		    submitButton.textContent = this._lcl.SEARCH;
//		    submitButton.id = 'submitButton';
//		    submitButton.style.cssText = 'margin-top: 20px; background-color: #20365b; color: white; padding: 6px 12px; border: none; border-radius: 4px; cursor: pointer; font-size: 18px; font-weight: bold;';
//		    container.appendChild(submitButton);
//
//		    // Event listener for the button
//		    submitButton.addEventListener('click', function() {
//		        var searchProperties = {};
//		        var inputs = container.querySelectorAll('input[type=text], input[type=date]');
//
//		        inputs.forEach(function(input) {
//		            var key = input.getAttribute('data-symbolic-name');
//		            var keyType = input.getAttribute('data-symbolic-name-dataType');
//		            if (input !== titleInput && input.value.trim() !== '') { // Check if input value is not empty
//		                searchProperties[key] = [input.value,keyType];
//		            }
//		        });
//
//		        var inputData = {
//		            classSymbolicName: classSymbolicName,
//		            searchProperties: searchProperties
//		        };
//
//		        // Add the titleInput data separately to searchProperties
//		        if (titleInput.value.trim() !== '') {
//		            var docTitlekeyType = titleInput.getAttribute('data-symbolic-name-dataType');
//		            inputData.searchProperties['DocumentTitle'] = [titleInput.value,docTitlekeyType];
//		        }
//
//		        that.saveSearchProperties(inputData.classSymbolicName,inputData.searchProperties)
//		        that.generalSearch(inputData.classSymbolicName,inputData.searchProperties);
//		        
//		    });
//		},

		
		getClassCustomProperty: function(classSymbolicName) {
		    var that = this;
		    var data = this.getClassCustomPropertyData(classSymbolicName);

		    var container = document.getElementById('ProertyDataGrideDiv');
		    if (!container) {
		        console.error('ProertyDataGrideDiv is not a valid DOM element');
		        return;
		    }

		    // Clear existing content
		    container.innerHTML = '';

		    // Create the fixed Document Title field
		    var titleContainer = document.createElement('div');
		    titleContainer.style.marginBottom = '10px';

		    var titleLabel = document.createElement('label');
		    titleLabel.textContent = this._lcl.DOC_TITLE;
		    titleLabel.style.marginRight = '5px';
		    titleContainer.appendChild(titleLabel);

		    var titleInput = document.createElement('input');
		    titleInput.type = 'text';
		    titleInput.setAttribute('data-symbolic-name', 'DocumentTitle');
		    titleInput.setAttribute('data-symbolic-name-dataType', "String");
		    titleInput.style.padding = '5px';
		    titleInput.style.border = '1px solid #ccc';
		    titleInput.style.borderRadius = '4px';
		    titleInput.style.width = 'calc(100% - 60px)';
		    titleContainer.appendChild(titleInput);

		    container.appendChild(titleContainer);

		    // Create a row to hold pairs of elements
		    var rowDiv = document.createElement('div');
		    rowDiv.style.display = 'flex';
		    rowDiv.style.justifyContent = 'space-between';
		    rowDiv.style.flexWrap = 'wrap';

		    data.forEach(function(item, index) {
		        var dataType = item.dataType;
		        var symbolicName = item.symbolicName; // Define symbolicName here
		        var choiceList = item.isChoiceList;

		        var inputContainer = document.createElement('div');
		        inputContainer.style.flex = '1 1 45%';
		        inputContainer.style.marginBottom = '10px';

		        var label = document.createElement('label');
		        label.textContent = item.displayName;
		        label.style.marginRight = '5px';
		        inputContainer.appendChild(label);

		        var element;

		        // Check if the field is a choice list
		        if (choiceList) {
		            element = document.createElement('select');
		            item.choiceListBeans.forEach(function(option) {
		                var optionElement = document.createElement('option');
		                optionElement.value = option.value;
		                optionElement.textContent = option.dispName;
		                element.appendChild(optionElement);
		            });
		        } else if (["String", "Integer", "Float"].includes(dataType)) {
		            element = document.createElement('input');
		            element.type = 'text';
		        } else if (dataType === "Date") {
		            element = document.createElement('input');
		            element.type = 'date';
		        }

		        if (element) {
		            element.setAttribute('data-symbolic-name', symbolicName);
		            element.setAttribute('data-symbolic-name-dataType', dataType);
		            element.style.padding = '5px';
		            element.style.border = '1px solid #ccc';
		            element.style.borderRadius = '4px';
		            element.style.width = 'calc(100% - 60px)';
		            inputContainer.appendChild(element);
		        }

		        rowDiv.appendChild(inputContainer);

		        if (index % 2 === 1 || index === data.length - 1) {
		            container.appendChild(rowDiv);
		            rowDiv = document.createElement('div');
		            rowDiv.style.display = 'flex';
		            rowDiv.style.justifyContent = 'space-between';
		            rowDiv.style.flexWrap = 'wrap';
		        }
		    });

		    var submitButton = document.createElement('button');
		    submitButton.textContent = this._lcl.SEARCH;
		    submitButton.id = 'submitButton';
		    submitButton.style.cssText = 'margin-top: 20px; background-color: #20365b; color: white; padding: 6px 12px; border: none; border-radius: 4px; cursor: pointer; font-size: 18px; font-weight: bold;';
		    container.appendChild(submitButton);

		    // Event listener for the button
		    submitButton.addEventListener('click', function() {
		        var searchProperties = {};
		        var inputs = container.querySelectorAll('input[type=text], input[type=date], select');

		        inputs.forEach(function(input) {
		            var key = input.getAttribute('data-symbolic-name');
		            var keyType = input.getAttribute('data-symbolic-name-dataType');
		            if (input !== titleInput && input.value.trim() !== '') { // Check if input value is not empty
		                if (input.tagName.toLowerCase() === 'select') {
//		                    searchProperties[key] = [input.options[input.selectedIndex].text, keyType];
		                    searchProperties[key] = [input.options[input.selectedIndex].value, keyType];

		                } else {
		                    searchProperties[key] = [input.value, keyType];
		                }
		            }
		        });

		        var inputData = {
		            classSymbolicName: classSymbolicName,
		            searchProperties: searchProperties
		        };

		        // Add the titleInput data separately to searchProperties
		        if (titleInput.value.trim() !== '') {
		            var docTitlekeyType = titleInput.getAttribute('data-symbolic-name-dataType');
		            inputData.searchProperties['DocumentTitle'] = [titleInput.value, docTitlekeyType];
		        }
		        that.saveSearchProperties(inputData.classSymbolicName, inputData.searchProperties)
		        that.generalSearch(inputData.classSymbolicName, inputData.searchProperties);

		    });
		},


	saveSearchProperties: function(classSymbolicName,searchProperties) {
		var toaster = new Toaster();
		params = {
					method: "AuditGenerator",
					repositoryId : ecm.model.desktop.getDefaultRepository().id,
					classSymbolicName : classSymbolicName,
					searchProperties : JSON.stringify(searchProperties),
					currentUserId :ecm.model.desktop.userId
			};
	     
		var response = ecm.model.Request.invokeSynchronousPluginService("SearchPlugin", "AdvancedFileSearchService",
				params);	
		var resultSet = new ResultSet(response);
	
		var results = [];
		if(!resultSet.result.startsWith("ERROR")){
		} else {
			if (resultSet.result.includes("(ACCESS DENIED)")) {
				toaster.redToaster(lcl.ACCESS_DENIED);						
			} else {
				toaster.redToaster(lcl.FAILED_TO_FETCH_DATA);
			}
			console.log("Failed to load data!");
			console.log(resultSet);
		}
	},
                           

		
		/**
		 * get class custom property
		 */
		getClassCustomPropertyData : function(symblolicName) {
			params = {
				method : "GetClassProperty",
				repositoryId : ecm.model.desktop.getDefaultRepository().id,
				classSymbolicName : symblolicName
			};

			var response = ecm.model.Request.invokeSynchronousPluginService("SearchPlugin", "AdvancedFileSearchService",
					params);
			var resultSet = new ResultSet(response);

			var results = [];
			if (!resultSet.result.startsWith("ERROR")) {
				results = json.parse(resultSet.result, true);
			} else {
				
				if (resultSet.result.includes("(ACCESS DENIED)")) {
					this.toaster.redToaster(lcl.ACCESS_DENIED);						
				}else  if(resultSet.result.includes("(FileNetException)")){
					if(resultSet.result.includes("E_ACCESS_DENIED")){
						this.toaster.redToaster(lcl.E_ACCESS_DENIED);
					}else if(resultSet.result.includes("E_OBJECT_NOT_FOUND")){
						this.toaster.redToaster(lcl.E_OBJECT_NOT_FOUND);
					}
					else if(resultSet.result.includes("E_BAD_CLASSID")){
						this.toaster.redToaster(lcl.E_BAD_CLASSID);
					}
					else{
						this.toaster.redToaster(lcl.FAILED_TO_FETCH_DATA);
					}
				}
				else {
					this.toaster.redToaster(lcl.FAILED_TO_FETCH_DATA);
				}
				console.log("Failed to load data!");
				console.log(resultSet);
			}
			return results;
		},
		
		
		generalSearch :function(classSymbolicName,searchProperties){
			this.clearViewSearch();
			var params = {};
			params.operation = 'advancedSearch';
			params.classSymbolicName=classSymbolicName;
			params.searchProperties=searchProperties;
			_this=this;
			params.parent=_this;
			var advancedFileSearchResults = new AdvancedFileSearchResults(params);
			this.viewContainer.addChild(advancedFileSearchResults);
			this.viewContainer.resize();
		},
		
		
		
		/**
		 * clear home container when click to menu 
		 */
		clearViewSearch: function() {
			if (this.viewContainer.getChildren() && this.viewContainer.getChildren().length > 0) {
				this.viewContainer.getChildren().forEach(function(child){
					child.destroyRecursive();
				});
			}
		},
		
		/**
		 * clear home container when click to menu
		 */
		clearView : function() {
			
			if (_parent.viewContainer.getChildren() && _parent.viewContainer.getChildren().length > 0) {
				_parent.viewContainer.getChildren().forEach(function(child) {
					child.destroyRecursive();
				});
			}
		},
		
		getDataStore : function(data) {
			var storeData = {
				items : data
			};
			var store = new dojo.data.ItemFileReadStore({
				data : storeData
			});
			return store;
		},
		


	});

});