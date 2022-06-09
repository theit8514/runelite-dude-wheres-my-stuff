package dev.thource.runelite.dudewheresmystuff.stash;

import dev.thource.runelite.dudewheresmystuff.ItemContainerWatcher;
import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Storage;
import java.util.Objects;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;

/** StashStorage is responsible for tracking storages that the players STASH units. */
@Getter
public class StashStorage extends Storage<StashStorageType> {

  private final StashUnit stashUnit;
  private boolean handleWithdrawOnTick = false;
  private boolean handleDepositOnTick = false;

  protected StashStorage(
      Client client, ClientThread clientThread, ItemManager itemManager, StashUnit stashUnit) {
    super(StashStorageType.STASH, client, clientThread, itemManager);
    this.stashUnit = stashUnit;
  }

  @Override
  public boolean onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return false;
    }

    if (stashUnit
            .getStashUnitData()
            .getWorldPoints()[0]
            .distanceTo(client.getLocalPlayer().getWorldLocation())
        > 10) {
      return false;
    }

    if (Objects.equals(chatMessage.getMessage(), "You withdraw your items from the STASH unit.")) {
      handleWithdrawOnTick = true;
    } else if (Objects.equals(
        chatMessage.getMessage(), "You deposit your items into the STASH unit.")) {
      handleDepositOnTick = true;
    }

    return true;
  }

  @Override
  public boolean onGameTick() {
    if (handleWithdrawOnTick && handleDepositOnTick) {
      return false;
    }

    final boolean updated = handleWithdrawOnTick || handleDepositOnTick;
    if (handleWithdrawOnTick) {
      handleWithdraw();
    }
    if (handleDepositOnTick) {
      handleDeposit();
    }

    handleWithdrawOnTick = false;
    handleDepositOnTick = false;
    return updated;
  }

  private void handleWithdraw() {
    lastUpdated = System.currentTimeMillis();
    items.clear();
  }

  private void handleDeposit() {
    lastUpdated = System.currentTimeMillis();
    for (ItemStack itemStack :
        ItemContainerWatcher.getInventoryWatcher().getItemsRemovedLastTick()) {
      for (ItemRequirement itemRequirement : stashUnit.getItemRequirements()) {
        if (itemRequirement.fulfilledBy(itemStack.getId())) {
          items.add(itemStack);
          break;
        }
      }
    }
  }

  @Override
  public String getName() {
    return stashUnit.getLocationName();
  }

  @Override
  protected String getConfigKey(String managerConfigKey) {
    return managerConfigKey + "." + stashUnit.getStashUnitData().getObjectId();
  }
}