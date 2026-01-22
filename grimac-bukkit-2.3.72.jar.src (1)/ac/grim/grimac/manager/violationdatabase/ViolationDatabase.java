package ac.grim.grimac.manager.violationdatabase;

import ac.grim.grimac.player.GrimPlayer;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface ViolationDatabase {
  void connect() throws SQLException;
  
  void logAlert(GrimPlayer paramGrimPlayer, String paramString1, String paramString2, String paramString3, int paramInt);
  
  int getLogCount(UUID paramUUID);
  
  List<Violation> getViolations(UUID paramUUID, int paramInt1, int paramInt2);
  
  void disconnect();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\violationdatabase\ViolationDatabase.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */