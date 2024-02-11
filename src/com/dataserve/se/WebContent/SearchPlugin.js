require(["dojo/_base/declare",
         "dojo/_base/lang",
         "searchPluginDojo/LinkDocumentsDialog",
         
         ], 
function(declare, lang, LinkDocumentsDialog ) {		
	/**
	 * Use this function to add any global JavaScript methods your plug-in requires.
	 */
	debugger
//lang.setObject("linkDocumentsAction", function(repository, items, callback, teamspace, resultSet, parameterMap) {
//	 
//	var _this = this;
//	params = {};
//	params.style = "width:100%; height: 100%";
//	params.fitContentArea = true;
//	params.lockFullscreen= false;
//	params.parent = _this;
//	params.docGuid = items[0].id.split(',')[2].replace("{", "").replace("}","");
//	params.docSympolicName = items[0].template;
//	params.filename =  items[0].name;
//	params.template_label =  items[0].template_label;
//	linkDocumentsDialog = new LinkDocumentsDialog(params);
//	linkDocumentsDialog.show(_this);
//});
lang.setObject("linkAction", function(repository, items, callback, teamspace, resultSet, parameterMap) {
		
		var _this = this;
		params = {};
		params.style = "width:100%; height: 100%";
		params.fitContentArea = true;
		params.lockFullscreen= false;
		params.parent = _this;
		params.docGuid = items[0].id.split(',')[2].replace("{", "").replace("}","");
		params.docSympolicName = items[0].template;
		params.filename =  items[0].name;
		params.template_label =  items[0].template_label;
		mostUsingDialog = new LinkDocumentsDialog(params);
		mostUsingDialog.show(_this);
});
});
