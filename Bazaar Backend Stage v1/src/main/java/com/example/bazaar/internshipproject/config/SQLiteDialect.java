//package com.example.bazaar.internshipproject.config;
//
//import org.hibernate.dialect.Dialect;
//import org.hibernate.dialect.identity.IdentityColumnSupport;
//import org.hibernate.dialect.identity.IdentityColumnSupportImpl;
//import org.hibernate.type.StandardBasicTypes;
//
//import java.sql.Types;
//
//public class SQLiteDialect extends Dialect {
//    public SQLiteDialect() {
//        super();
//        // Use appropriate methods to register column types.
//        registerColumnTypes(Types.INTEGER, StandardBasicTypes.INTEGER.getName());
//        registerColumnTypes(Types.VARCHAR, StandardBasicTypes.STRING.getName());
//        registerColumnTypes(Types.BIGINT, StandardBasicTypes.BIG_INTEGER.getName());
//        registerColumnTypes(Types.BOOLEAN, StandardBasicTypes.BOOLEAN.getName());
//        registerColumnTypes(Types.FLOAT, StandardBasicTypes.FLOAT.getName());
//        registerColumnTypes(Types.DOUBLE, StandardBasicTypes.DOUBLE.getName());
//        registerColumnTypes(Types.DECIMAL, StandardBasicTypes.BIG_DECIMAL.getName());
//        registerColumnTypes(Types.DATE, StandardBasicTypes.DATE.getName());
//        registerColumnTypes(Types.TIME, StandardBasicTypes.TIME.getName());
//        registerColumnTypes(Types.TIMESTAMP, StandardBasicTypes.TIMESTAMP.getName());
//    }
//
//    @Override
//    public boolean supportsIdentityColumns() {
//        return true;
//    }
//
//    @Override
//    public IdentityColumnSupport getIdentityColumnSupport() {
//        return new IdentityColumnSupportImpl();
//    }
//
//    @Override
//    public boolean hasDataTypeInIdentityColumn() {
//        return false;
//    }
//
//    @Override
//    public boolean supportsSequences() {
//        return false;
//    }
//
//    @Override
//    public boolean supportsInsertSelectIdentity() {
//        return true;
//    }
//}
//
//
////public class SQLiteDialect extends Dialect {
////
////    public SQLiteDialect() {
////        super();
////        // Register column types with proper data types for SQLite
////        registerColumnType(Types.INTEGER, "integer");
////        registerColumnType(Types.VARCHAR, "text");
////        registerColumnType(Types.BIGINT, "bigint");
////        registerColumnType(Types.BOOLEAN, "integer");
////        registerColumnType(Types.FLOAT, "real");
////        registerColumnType(Types.DOUBLE, "real");
////        registerColumnType(Types.DECIMAL, "numeric");
////        registerColumnType(Types.DATE, "date");
////        registerColumnType(Types.TIME, "time");
////        registerColumnType(Types.TIMESTAMP, "timestamp");
////    }
////
////    @Override
////    public boolean supportsIdentityColumns() {
////        return true;
////    }
////
////    @Override
////    public IdentityColumnSupport getIdentityColumnSupport() {
////        return new IdentityColumnSupportImpl();
////    }
////
////    @Override
////    public boolean hasDataTypeInIdentityColumn() {
////        return false;
////    }
////
////    @Override
////    public boolean supportsSequences() {
////        return false;
////    }
////
////    @Override
////    public boolean supportsInsertSelectIdentity() {
////        return true;
////    }
////}
////
////
//////
//////import org.hibernate.dialect.Dialect;
//////import org.hibernate.dialect.function.SqlFunction;
//////import org.hibernate.dialect.function.StandardSQLFunction;
//////import org.hibernate.dialect.function.VarArgsSQLFunction;
//////import org.hibernate.type.StringType;
//////
//////import java.sql.Types;
//////
//////public class SQLiteDialect extends Dialect {
//////
//////    public SQLiteDialect() {
//////        registerColumnType(Types.BIT, "integer");
//////        registerColumnType(Types.TINYINT, "tinyint");
//////        registerColumnType(Types.SMALLINT, "smallint");
//////        registerColumnType(Types.INTEGER, "integer");
//////        registerColumnType(Types.BIGINT, "bigint");
//////        registerColumnType(Types.FLOAT, "float");
//////        registerColumnType(Types.REAL, "real");
//////        registerColumnType(Types.DOUBLE, "double");
//////        registerColumnType(Types.NUMERIC, "numeric");
//////        registerColumnType(Types.DECIMAL, "decimal");
//////        registerColumnType(Types.CHAR, "char");
//////        registerColumnType(Types.VARCHAR, "varchar");
//////        registerColumnType(Types.LONGVARCHAR, "longvarchar");
//////        registerColumnType(Types.DATE, "date");
//////        registerColumnType(Types.TIME, "time");
//////        registerColumnType(Types.TIMESTAMP, "timestamp");
//////        registerColumnType(Types.BINARY, "blob");
//////        registerColumnType(Types.VARBINARY, "blob");
//////        registerColumnType(Types.LONGVARBINARY, "blob");
//////        registerColumnType(Types.BLOB, "blob");
//////        registerColumnType(Types.CLOB, "clob");
//////        registerColumnType(Types.BOOLEAN, "integer");
//////
//////        registerFunction("concat", new VarArgsSQLFunction(StringType.INSTANCE, "", "||", ""));
//////        registerFunction("mod", new SQLFunctionTemplate(StringType.INSTANCE, "?1 % ?2"));
//////        registerFunction("substr", new StandardSQLFunction("substr", StringType.INSTANCE));
//////        registerFunction("substring", new StandardSQLFunction("substr", StringType.INSTANCE));
//////    }
//////
//////    public boolean supportsIdentityColumns() {
//////        return true;
//////    }
//////
//////    public boolean hasDataTypeInIdentityColumn() {
//////        return false;
//////    }
//////
//////    public String getIdentityColumnString() {
//////        return "integer";
//////    }
//////
//////    public String getIdentitySelectString() {
//////        return "select last_insert_rowid()";
//////    }
//////
//////    public boolean supportsLimit() {
//////        return true;
//////    }
//////
//////    protected String getLimitString(String query, boolean hasOffset) {
//////        return new StringBuffer(query.length() + 20).append(query).append(hasOffset ? " limit ? offset ?" : " limit ?")
//////                .toString();
//////    }
//////
//////    public boolean supportsTemporaryTables() {
//////        return true;
//////    }
//////
//////    public String getCreateTemporaryTableString() {
//////        return "create temporary table if not exists";
//////    }
//////
//////    public boolean dropTemporaryTableAfterUse() {
//////        return false;
//////    }
//////
//////    public boolean supportsCurrentTimestampSelection() {
//////        return true;
//////    }
//////
//////    public boolean isCurrentTimestampSelectStringCallable() {
//////        return false;
//////    }
//////
//////    public String getCurrentTimestampSelectString() {
//////        return "select current_timestamp";
//////    }
//////
//////    public boolean supportsUnionAll() {
//////        return true;
//////    }
//////
//////    public boolean hasAlterTable() {
//////        return false;
//////    }
//////
//////    public boolean dropConstraints() {
//////        return false;
//////    }
//////
//////    public String getAddColumnString() {
//////        return "add column";
//////    }
//////
//////    public String getForUpdateString() {
//////        return "";
//////    }
//////
//////    public boolean supportsOuterJoinForUpdate() {
//////        return false;
//////    }
//////
//////    public String getDropForeignKeyString() {
//////        throw new UnsupportedOperationException("No drop foreign key syntax supported by SQLiteDialect");
//////    }
//////
//////    public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable,
//////                                                   String[] primaryKey, boolean referencesPrimaryKey) {
//////        throw new UnsupportedOperationException("No add foreign key syntax supported by SQLiteDialect");
//////    }
//////
//////    public String getAddPrimaryKeyConstraintString(String constraintName) {
//////        throw new UnsupportedOperationException("No add primary key syntax supported by SQLiteDialect");
//////    }
//////
//////    public boolean supportsIfExistsBeforeTableName() {
//////        return true;
//////    }
//////
//////    public boolean supportsCascadeDelete() {
//////        return false;
//////    }
//////}