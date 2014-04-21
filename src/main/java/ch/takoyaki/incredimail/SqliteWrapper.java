package ch.takoyaki.incredimail;

import static java.lang.String.format;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqliteWrapper {
	public interface Transformer<R> {
		R apply(ResultSet t) throws SQLException;
	}

	public static <T> List<T> query(File db, String query,
			Transformer<T> transform) {
		loadDriver();
		List<T> result = new ArrayList<>();
		try {
			try (Connection c = getConnection(db);
					Statement stmt = c.createStatement();
					ResultSet rs = stmt.executeQuery(query)) {
				while (rs.next()) {
					T v = transform.apply(rs);
					if (v != null) {
						result.add(v);
					}
				}
			}
		} catch (SQLException e) {
			throw new IllegalStateException(format("Error executing query %s",
					query), e);
		}
		return result;
	}

	private static void loadDriver() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Couldn't load sqlite driver", e);
		}
	}

	private static Connection getConnection(File db) throws SQLException {
		Connection c = DriverManager.getConnection(format("jdbc:sqlite:%s",
				db.getAbsoluteFile()));
		c.setAutoCommit(false);
		return c;
	}
}
