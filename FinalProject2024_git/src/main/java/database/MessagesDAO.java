package database;

import model.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessagesDAO {

    private static final String INSERT_MESSAGES_SQL = "INSERT INTO messages (username, message, group_id) VALUES (?, ?, ?)";
    private static final String SELECT_MESSAGES_BY_GROUP_ID = "SELECT * FROM messages WHERE group_id = ? ORDER BY created_at ASC";

    public static void insertMessage(Message message) {
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_MESSAGES_SQL)) {
            preparedStatement.setString(1, message.getUsername());
            preparedStatement.setString(2, message.getMessage());
            preparedStatement.setString(3, message.getGroupId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Message> getMessagesForGroup(String groupId) {
        List<Message> messages = new ArrayList<>();
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MESSAGES_BY_GROUP_ID)) {
            preparedStatement.setString(1, groupId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Message message = new Message(
                    rs.getString("username"),
                    rs.getString("message"),
                    rs.getString("group_id"),
                    rs.getTimestamp("created_at")
                );
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
