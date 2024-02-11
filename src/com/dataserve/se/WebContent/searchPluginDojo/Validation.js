define([
    "dojo/_base/declare",
    "dojo/_base/lang","dojox/validate", "ecm/model/ResultSet", "dojo/json"
], 
function(declare, lang,validate, ResultSet, json) {
	
	return declare("administrationpluginDojo.Validation", [], {
	
		validateName: function(name, isArabic) {
			if (!name) {
				return false;
			}
			
			if (typeof name === 'string' || name instanceof String) {
				name = name.trim();
			} else {
				return false;
			}
			
			var pattern;
			if (isArabic) {
				pattern = /^[0-9\u0621-\u064A\u0660-\u0669\s_ ]{1,100}$/g;
			} else{
				pattern = /^[a-zA-Z0-9\u0660-\u0669\s_ ]{1,100}$/g;
			}
			
			return pattern.test(name);
		},
	
		validateTextArea: function(name) {
			if (!name) {
				return false;
			}
			
			name = name.trim();
			if (name === "") {
				return false;
			}
			
			if (name.length < 3 || name.length > 500) {
				return false;
			}
			
			return true;
		},
		
		validateInt: function(value) {
			if (value == null) {
				return false;
			}
			
			if (value.includes(".")) {
				return false;
			}
			value = value.replaceAll(",", "");
			if (isNaN(value)) {
				return false;
			}
			
			intValue = parseInt(value);
			if (intValue > 2147483647) {
				return false;
			}
			
			return true;
		},
		
		validateEmail: function(value) {
			if(value == ''){
				return true;
		    }
		    return dojox.validate.isEmailAddress(value);			
		},
		
		validateDesignName: function(name) {
			if (!name) {
				return false;
			}
			
			name = name.trim();
			if (name === "") {
				return false;
			}
			
			var pattern = /^[a-zA-Z0-9_\-\.]+$/;
			return pattern.test(name);
		},
	
	});
   
});