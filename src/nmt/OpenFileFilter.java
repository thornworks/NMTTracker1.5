package nmt;

import java.io.File;
import javax.swing.filechooser.*;

class OpenFileFilter extends FileFilter {
	
	public String fileExt;
	
	public OpenFileFilter() {
		this(".csv");
	}
	
	public OpenFileFilter(String extension) {
		fileExt = extension;
	}
	
	public boolean accept(File f) {
		
		if(f.isDirectory())
			return true;
		
		else {
			
			if(f.getName().toLowerCase().endsWith(fileExt))
				return true;
		}
		
		return false;
	}
	
	public String getDescription() {
		return ("NMT Data (*" + fileExt + ")");
	}
}
	