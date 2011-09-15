function getGreenstone() {
	$.get("http://192.168.1.4:8080/greenstone3/dev?a=b&rt=s&s=ClassifierBrowse&c=kimt&cl=CL1", function(data){
		alert("Data Loaded: " + data);
	});
}