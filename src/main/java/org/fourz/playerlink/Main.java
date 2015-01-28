/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Justin W. Flory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.fourz.playerlink;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;

public final class Main extends JavaPlugin {
	
	private long repeatWait;
	
    @Override
    public void onEnable() {
        getLogger().info("PlayerLink has been enabled!");

        try {
            File database = new File(getDataFolder(), "config.yml");
            if (!database.exists()) saveDefaultConfig();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        // Submit plugin metrics
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }

//        if (this.getConfig().getBoolean("auto-update")) {
//            @SuppressWarnings("unused")
//            Updater updater = new Updater(this, 54020, this.getFile(), Updater.UpdateType.DEFAULT, true);
//        }
        this.setRepeatWait(this.getConfig().getLong("repeat-wait") * 1000);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {    	
    	if (sender instanceof Player) {
    		Player p = (Player) sender;
    		String msg = null;
    		String c = cmd.getName().toLowerCase();
    		Boolean c_match = true;
    		switch (c) {
    		case "website":
    			msg = this.getConfig().getString("server-name") + "'s Website Address!";
    			break;
    		case "vote":
    			msg = "Vote for " + this.getConfig().getString("server-name");
    			break;
    		case "donate":
    			msg = "Donate to " + this.getConfig().getString("server-name");
    			break;        		
    		case "forums":
    			msg = this.getConfig().getString("server-name") + "'s Forums!";
    			break;
    		case "barter":
    			c = "shop";
    			msg = this.getConfig().getString("server-name") + "'s Server Shop!";        		
    			break;
    		case "shop":        		
    			msg = this.getConfig().getString("server-name") + "'s Server Shop!";
    			break;
    		case "teamspeak":
    			c = "voice";
    			msg = this.getConfig().getString("server-name") + "'s Voice Server IP!" ;
    			break;        		
    		case "voice": 
    			msg = this.getConfig().getString("server-name") + "'s Voice Server IP!" ;
    			break;
    		case "map":        		        		
    			msg = this.getConfig().getString("server-name") + "'s DynMap page!";
    			break;
    		case "wiki":
    			msg = this.getConfig().getString("server-name") + "'s Wiki!";
    			break;
    		case "facebook":
    			msg = this.getConfig().getString("server-name") + "'s Facebook Page!";
    			break;
    		case "twitter":	 
    			msg = this.getConfig().getString("server-name") + "'s Twitter Page!";
    			break;
    		case "youtube":	 
    			msg = this.getConfig().getString("server-name") + "'s YouTube Page!";
    			break;
    		case "google+":
    			msg = this.getConfig().getString("server-name") + "'s Google+ Page!";
    			break;
    		case "link":
    			msg = this.getConfig().getString("server-name") + "'s " + this.getConfig().getString("link.name") + "!";

    		default:
    			c_match = false;
    		}

    		if (c_match == true && cmd.getName().equals(c) && p.hasPermission("playerlink." + c)) {
    			p.sendMessage(ChatColor.GOLD + "> " + ChatColor.YELLOW + msg + ChatColor.GOLD + " <");
    			p.sendMessage(ChatColor.UNDERLINE + this.getConfig().getString(c + ".url"));            
    			if (this.getConfig().getBoolean(c + ".enable-broadcast")) {
    				long last = this.getConfig().getLong("last-used." + c, 0L);
    				long now = System.currentTimeMillis();                
    				if ((now - last) > this.getRepeatWait()) {                	
    					Bukkit.broadcastMessage(ChatColor.GREEN + p.getDisplayName() + ChatColor.GREEN + " used " + ChatColor.ITALIC + "/" + c + ChatColor.RESET + ChatColor.GREEN + " to get the " + c + " link for " + (this.getConfig().getString("server-name")));
    					this.getConfig().set("last-used." + c, now);
    					this.saveConfig();
    				}
    			}
    			return true;
    		}           

    		if (cmd.getName().equalsIgnoreCase("playerlink")) {
    			if (args.length != 1) {
    				return false;
    			} else if (args[0].equalsIgnoreCase("reload") && p.hasPermission("playerlink.reload")) {
    				this.reloadConfig();
    				sender.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
    				return true;
    			} else if (args[0].equalsIgnoreCase("help") && p.hasPermission("playerlink.help")) {
    				sender.sendMessage(ChatColor.GOLD + "=-=-=-=-=-=-=-> " + ChatColor.YELLOW + "PlayerLink Commands" + ChatColor.GOLD + " <-=-=-=-=-=-=-=");
    				sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "PL" + ChatColor.GOLD + "] " + ChatColor.RED + "/playerlink help: " + ChatColor.GOLD + "Shows commands in the plugin.");
    				sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "PL" + ChatColor.GOLD + "] " + ChatColor.RED + "/website: " + ChatColor.GOLD + "Displays a link to the server website.");
    				sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "PL" + ChatColor.GOLD + "] " + ChatColor.RED + "/vote: " + ChatColor.GOLD + "Displays a link to a voting website for this server.");
    				sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "PL" + ChatColor.GOLD + "] " + ChatColor.RED + "/donate: " + ChatColor.GOLD + "Displays a link to the donation page.");                
    				sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "PL" + ChatColor.GOLD + "] " + ChatColor.RED + "/forums: " + ChatColor.GOLD + "Displays a link to the server forums.");
    				sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "PL" + ChatColor.GOLD + "] " + ChatColor.RED + "/shop: " + ChatColor.GOLD + "Displays a link to the server shop.");
    				sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "PL" + ChatColor.GOLD + "] " + ChatColor.RED + "/voice: " + ChatColor.GOLD + "Displays an IP to the voice server.");
    				sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "PL" + ChatColor.GOLD + "] " + ChatColor.RED + "/map: " + ChatColor.GOLD + "Displays a link to the server DynMap.");                
    				sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "PL" + ChatColor.GOLD + "] " + ChatColor.RED + "/wiki: " + ChatColor.GOLD + "Displays a link to the server wiki.");
    				sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "PL" + ChatColor.GOLD + "] " + ChatColor.RED + "/facebook: " + ChatColor.GOLD + "Displays a link to the server Facebook page.");
    				sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "PL" + ChatColor.GOLD + "] " + ChatColor.RED + "/twitter: " + ChatColor.GOLD + "Displays a link to the server Twitter page.");
    				sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "PL" + ChatColor.GOLD + "] " + ChatColor.RED + "/youtube: " + ChatColor.GOLD + "Displays a link to the server YouTube page.");
    				sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "PL" + ChatColor.GOLD + "] " + ChatColor.RED + "/google+: " + ChatColor.GOLD + "Displays a link to the server Google+ page.");
    				sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "PL" + ChatColor.GOLD + "] " + ChatColor.RED + "/instagram: " + ChatColor.GOLD + "Displays a link to the server Instagram page");
    				sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "PL" + ChatColor.GOLD + "] " + ChatColor.RED + "/link: " + ChatColor.GOLD + "Displays a link to whatever an admin has set.");
    				sender.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "PL" + ChatColor.GOLD + "] " + ChatColor.RED + "/playerlink reload: " + ChatColor.GOLD + "Reload the configuration.");

    			}
    			return true;
    		} else {
    			p.sendMessage("You do not have permission to use that command.");
    		}
    		return false;	        
    	} else {
    		System.out.println("Cannot be run from console.");
    		return false;
    	}        

    }

    @Override
    public void onDisable() {
        getLogger().info("PlayerLink has been disabled!");
    }

	public long getRepeatWait() {
		return repeatWait;
	}

	public void setRepeatWait(long repeatWait) {
		this.repeatWait = repeatWait;
	}
    
}
