package onim.en.etl.api;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import onim.en.etl.api.dto.PlayerStatus;

public class PlayerStatusProvider {

  private ConcurrentHashMap<String, PlayerStatus> nameToStatus = new ConcurrentHashMap<>();
  private ConcurrentHashMap<UUID, PlayerStatus> idToStatus = new ConcurrentHashMap<>();

  public void setStatus(PlayerStatus status) {
    if (status == null) {
      return;
    }

    if (status.uuid == null || status.mcid == null) {
      return;
    }

    idToStatus.put(status.uuid, status);
    nameToStatus.put(status.mcid, status);
  }

  public PlayerStatus getStatusByUniqueId(UUID uniqueId) {
    return idToStatus.get(uniqueId);
  }

  public PlayerStatus getStatusByName(String name) {
    return nameToStatus.get(name);
  }

  public void clear() {
    nameToStatus.clear();
    idToStatus.clear();
  }
}
