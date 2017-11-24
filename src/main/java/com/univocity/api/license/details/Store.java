/*
 * Copyright (c) 2017 Univocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 *
 */

package com.univocity.api.license.details;

import com.univocity.api.common.*;

import java.util.*;

/**
 * Information required from a product store to enable license validation - both online and offline.
 */
public final class Store {

	private final String name;
	private final Long id;
	private final List<String> licenseServerDomains;

	/**
	 * Creates a new store details object, with ID, name and list of servers used for license validation.
	 *
	 * @param id                        the ID of your product store
	 * @param name                      the name of your store.
	 * @param licenseServerDomain       the domain name used by the license server.
	 * @param otherLicenseServerDomains additional domain names of license servers to be used for remote license validation.
	 */
	public Store(Long id, String name, String licenseServerDomain, String... otherLicenseServerDomains) {
		Args.positiveOrZero(id, "Store ID");
		Args.notBlank(name, "Store name");
		Args.notBlank(licenseServerDomain, "License server domain");
		Args.noBlanks(otherLicenseServerDomains, "License server domain list");

		this.id = id;
		this.name = name;

		licenseServerDomain = clearDomainName(licenseServerDomain);
		for (int i = 0; i < otherLicenseServerDomains.length; i++) {
			otherLicenseServerDomains[i] = clearDomainName(otherLicenseServerDomains[i]);
		}

		LinkedHashSet<String> set = new LinkedHashSet<String>();
		set.add(licenseServerDomain);
		Collections.addAll(set, otherLicenseServerDomains);
		set.remove("");

		for (String domain : set) {
			if (domain.contains("univocity.")) {
				throw new IllegalArgumentException("Can't use univocity.* as a license server");
			}
		}

		if (set.isEmpty()) {
			throw new IllegalArgumentException("A license server domain is mandatory.");
		}

		this.licenseServerDomains = Collections.unmodifiableList(new ArrayList<String>(set));
	}

	private static String clearDomainName(String value) {
		if (value == null) {
			return "";
		}
		value = value.trim().toLowerCase();

		for (int i = 0; i < value.length(); i++) {
			char ch = value.charAt(i);
			if (!(Character.isLetterOrDigit(ch) || ch == '.' || ch == '_' || ch == '-' || ch == ':')) {
				throw new IllegalArgumentException("'" + value + "' is not a valid domain name");
			}
		}

		return value;
	}


	/**
	 * Returns the name the store
	 *
	 * @return the store name
	 */
	public final String name() {
		return name;
	}

	/**
	 * The unique ID of the store
	 *
	 * @return the ID of the store
	 */
	public final Long id() {
		return id;
	}

	@Override
	public final String toString() {
		return name;
	}

	/**
	 * Returns the list of domains where a license server is available for remote license validation. Each domain name
	 * listed should have a {@code /licenses/validate} endpoint available, which will receive POST messages via HTTPS.
	 *
	 * The names in this list will be accessed randomly by the client software whenever the license needs to be validated.
	 *
	 * @return a unmodifiable list of license servers to use for remote license validation.
	 */
	public final List<String> licenseServerDomains() {
		return licenseServerDomains;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Store that = (Store) o;

		if (!name.equals(that.name)) return false;
		return id.equals(that.id);
	}

	@Override
	public final int hashCode() {
		int result = name.hashCode();
		result = 31 * result + id.hashCode();
		return result;
	}
}
