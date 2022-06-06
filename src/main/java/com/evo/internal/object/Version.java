package com.evo.internal.object;

import com.evo.exception.SimpleException;

public class Version {
	
	public static String versionPattern = "([0-9]+(?:[\\._][0-9]+)*)|(staged)";
	
	private String version;
	
	public Version(String version) throws SimpleException {
		if (!version.matches("^" + Version.versionPattern + "$"))
			throw new SimpleException("'" + version + "' is not a valid version.");
		this.version = version;
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public String getStandardizedVersion() {
		return this.version.replaceAll("_", ".");
	}
	
	public int compareTo(Version otherVersion) {
		return this.getStandardizedVersion().compareTo(otherVersion.getStandardizedVersion());
	}
	
	public boolean isStaged() {
		return this.version.equals("staged");
	}
	
	public boolean isHigherThan(Version otherVersion) {
		return this.compareTo(otherVersion) > 0;
	}
	
	public boolean isHigherOrEqualThan(Version otherVersion) {
		return this.compareTo(otherVersion) >= 0;
	}
	
	public boolean isEqualThan(Version otherVersion) {
		return this.compareTo(otherVersion) == 0;
	}
	
	public boolean isLowerOrEqualThan(Version otherVersion) {
		return this.compareTo(otherVersion) <= 0;
	}
	
	public boolean isLowerThan(Version otherVersion) {
		return this.compareTo(otherVersion) < 0;
	}
}
