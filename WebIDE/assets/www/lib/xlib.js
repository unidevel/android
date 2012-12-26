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

function XLibrary()
{
	this._pref = new XPrefence();
	this._dataDir = "/sdcard/data";
	this._rootDir = "/";
	this._rootFiles = ["dir1","dir2", "dir3", "1.txt", "2.xml"];
	this._dir1Files = ["a.txt", "b.txt"];
	this._dir2Files = ["1.txt", "2.txt"];
	this._dir3Files = ["s.txt", "d.txt"];
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
	this.listDir = function(dir)
	{
		if ( dir == this._rootDir || dir == this._dataDir || dir == this._dataDir+"/") 
		{
			return this._rootFiles;
		}
		else if ( dir.endsWith("dir1") ) 
		{
			return this._dir1Files;
		}
		else if ( dir.endsWith("dir2") ) 
		{
			return this._dir1Files;
		}
		else if ( dir.endsWith("dir3") ) 
		{
			return this._dir1Files;
		}
		return {};
	}
	this.read = function(file)
	{
		return "1234567890\n1234567890\n1234567890\n1234567890";
	}
	this.write = function(file)
	{
		return true;
	}
	this.view = function(url)
	{
		window.open(url, "_about:blank");
	}
	this.call = function(number)
	{
		alert("calling... "+number);
	}
	this.market = function(appId)
	{
		window.open("https://play.google.com/store/apps/details?id="+appId, "_about:blank");
	}
}

if (typeof(window.unidevel)=="undefined")
{
	alert("use xlib");
	window.unidevel = new XLibrary(); 
}
