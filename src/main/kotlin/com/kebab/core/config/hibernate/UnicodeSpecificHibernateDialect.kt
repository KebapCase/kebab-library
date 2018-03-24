package com.kebab.core.config.hibernate

import org.hibernate.dialect.MySQL5InnoDBDialect

/**
 * Extends MySQL5InnoDBDialect and sets the default charset and collation to be UTF-8
 * This dialect needed in case of having troubles with UTF-8 encoding in mysql db,
 * which is created by Hibernate.
 *
 * @author Valentin Trusevich
 */
class UnicodeSpecificHibernateDialect : MySQL5InnoDBDialect() {

    override fun getTableTypeString() = " ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_unicode_520_ci"
}
