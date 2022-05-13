package dev.thource.runelite.dudewheresmystuff.carryable;

import dev.thource.runelite.dudewheresmystuff.ItemStack;
import dev.thource.runelite.dudewheresmystuff.Storage;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

/**
 * CarryableStorage is responsible for tracking storages that the player can carry (looting bag,
 * rune pouch, etc).
 */
@Getter
public class CarryableStorage extends Storage<CarryableStorageType> {

  protected CarryableStorage(
      CarryableStorageType type,
      Client client,
      ClientThread clientThread,
      ItemManager itemManager) {
    super(type, client, clientThread, itemManager);
  }

  @Override
  public boolean onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
    boolean updated = super.onItemContainerChanged(itemContainerChanged);

    if (type == CarryableStorageType.EQUIPMENT && updated) {
      ItemStack empty = new ItemStack(-1, "empty", 1, 0, 0, false);

      if (items.size() > 13) {
        // move ammo into the correct place if the slot exists
        ItemStack ammo = items.remove(EquipmentInventorySlot.AMMO.getSlotIdx());
        items.add(3, ammo);
      } else {
        items.add(3, empty);
      }
      items.remove(12); // remove empty space between boots and ring

      // pad it out to fit the 4 wide grid
      items.add(0, empty);
      items.add(2, empty);
      items.add(3, empty);
      items.add(7, empty);
      items.add(11, empty);
      items.add(15, empty);

      items.forEach(itemStack -> itemStack.setId(itemManager.canonicalize(itemStack.getId())));
    }

    return updated;
  }
}