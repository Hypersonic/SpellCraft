package me.hgilman.Spells.Executors;

import me.hgilman.Spells.Spell;
import me.hgilman.Spells.SpellBook;
import me.hgilman.Spells.Spells;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.inventory.SpoutItemStack;



public class SpellCommandExecutor implements CommandExecutor {
	private static Spells plugin;

	public SpellCommandExecutor(Spells instance)
	{
		plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player player;
		if(sender instanceof Player)
		{
			player = (Player) sender;
			if(new SpoutItemStack(player.getItemInHand()).isCustomItem())
			{
				//SPELLINFO
				if(command.getName().equalsIgnoreCase("spellinfo"))
				{
					if(args.length==0)
					{
						Spell currentSpell = plugin.getPlayerData(player).getSpellBook().getCurrentSpell(); // Default to the current spell.
						sender.sendMessage("Current spell " + currentSpell.abilityFormat(true) + ": " + currentSpell.getDescription());
						return true;
					}
					else if (args.length==1) // They gave an arg.
					{
						SpellBook spellBook = plugin.getPlayerData(player).getSpellBook();
						Spell selectedSpell = spellBook.getSpell(args[0]);
						if(selectedSpell != null)
						{
							sender.sendMessage(selectedSpell.abilityFormat() + ": " + selectedSpell.getDescription());
							return true;
						}
						else
						{
							sender.sendMessage(ChatColor.DARK_RED + "Spell " + args[0] + " not found in your spellbook.");
							return true;
						}
					}
					else
					{
						return false;
					}
				}

				// LISTSPELLS
				else if(command.getName().equalsIgnoreCase("listspells"))
				{
					sender.sendMessage("Currently available spells (arrow denotes selection):");
					SpellBook spellBook = plugin.getPlayerData(player).getSpellBook();
					for (Spell spell : spellBook.getRegistry())
					{
						if (spell == spellBook.getCurrentSpell())
						{
							sender.sendMessage("   - " + spell.abilityFormat() + " <--"); // It's the current spell.

						}
						else
						{
							sender.sendMessage("   - " + spell.abilityFormat()); // It's not the current spell.
						}
					}
					sender.sendMessage("Key: " + ChatColor.DARK_GREEN + "(proper resources)" + ChatColor.DARK_RED + " (needs materials)");
					return true;
				}

				// SETSPELL
				else if(command.getName().equalsIgnoreCase("setspell"))
				{
					if (args.length==1)
					{
						SpellBook spellBook = plugin.getPlayerData(player).getSpellBook();
						Spell selectedSpell = spellBook.getSpell(args[0]);
						if(selectedSpell != null)
						{
							spellBook.setCurrentSpell(selectedSpell);
							sender.sendMessage("Current spell set to " + selectedSpell.abilityFormat() + ".");
							return true;
						}
						else
						{
							sender.sendMessage(ChatColor.DARK_RED + "Spell " + args[0] + " not found in your spellbook.");
							return true;
						}
					}
					else
					{
						return false;
					}
				}
				else
				{
					plugin.log.info(this.toString() + " could not process the commmand: " + command.getName());
					return false;
				}
			}
			else
			{
				sender.sendMessage(ChatColor.DARK_RED + "You must be wielding a Golden Scepter to use Spells.");
				return true;
			}
		}
		else
		{
			plugin.log.info(sender.getName() + " tried to use an ingame-only command.");
			return false; // Only ingame players may use the spell related commands.
		}
	}



}
