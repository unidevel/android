function XPrefence()
{
	this.data = {};
	this.putString = function(name, value)
	{
		this.data[name] = value;
	}
	this.getString = function(name, defaultValue)
	{
		var value = this.data[name];
		if ( value == null )
			return defaultValue;
		return value;
	}
	this.contains = function(name)
	{
		return this.data[name] != null;
	}
}
String.prototype.endsWith = function(suffix) {
    return this.indexOf(suffix, this.length - suffix.length) !== -1;
};
function XLibrary()
{
	this._pref = new XPrefence();
	this._dataDir = "/sdcard/data";
	this._rootDir = "/";
	this._rootFiles = "[[\"dir1\",true,0,0],[\"dir2\",true,0,0], [\"1.txt\",false,44,8]]";
	this._rootDirs = "[\"dir1\",\"dir2\"]";
	this._dir1Files = "[[\"a.txt\",false,44,8]]";
	this._dir2Files = "[[\"1.txt\",false,44,8]]";
	this.pref = function()
	{
		return new XPrefence();
	}
	this.getPref = function(name)
	{
		return this._pref.getString(name, null);
	}
	this.putPref = function(name, value)
	{
		this._pref.putString(name, value);
	}
	this.dataDir = function()
	{
		return this._dataDir;
	}
	this.rootDir = function()
	{
		return this._rootDir;
	}
	this.getFile = function(name)
	{
		return this._dataDir+"/"+name;
	}
	this.isFile = function(path)
	{
		return path.indexOf(".") > 0; 
	}
	this.listDirs = function(dir)
	{
		if ( dir == this._rootDir || dir == this._dataDir || dir == this._dataDir+"/") 
		{
			return this._rootDirs;
		}
	}
	this.listFiles = function(dir)
	{
		if ( dir.endsWith("dir1") || dir.endsWith("dir1/") ) 
		{
			return this._dir1Files;
		}
		else if ( dir.endsWith("dir2")|| dir.endsWith("dir2/") ) 
		{
			return this._dir1Files;
		}
		return this._rootFiles;
	}
	this.load = function(file)
	{
		return "1234567890\n1234567890\n1234567890\n1234567890";
	}
	this.save = function(file)
	{
		return true;
	}
	this.view = function(url)
	{
		window.open(url, "_blank");
	}
	this.call = function(number)
	{
		alert("calling... "+number);
	}
	this.market = function(appId)
	{
		window.open("https://play.google.com/store/apps/details?id="+appId, "_blank");
	}
	this.map = function(x,y)
	{
		window.open("https://maps.google.com/maps?ll="+x+","+y, "_blank");
	}
	this.select = function(type, callback)
	{
		window.eval(callback+"(\"/123\")");
	}
	this.selectImage = function(callback)
	{
		window.eval(callback+"(\"/123.jpg\")");
	}
	this.exit=function()
	{
		alert("exit");
	}
	this.bind=function(event,func)
	{
		// not implemented
	}
	this.toast=function(msg){
		alert(msg);
	}
}

if (typeof(window.unidevel)=="undefined")
{
	alert("use xlib");
	window.unidevel = new XLibrary(); 
}
