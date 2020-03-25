package sample.plugin;

import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;

import com.flowpowered.math.vector.Vector3i;
// Imports for logger
import com.google.inject.Inject;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;

@Plugin(id = "sampleplugin", name = "Sample Plugin", version = "0.0.1", description = "Example")
public class Simon {
	
	private static final String PLAYER_NAME = "playerName";

    @Inject
    private Logger log;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        log.info("Successfully running ExamplePlugin!!!");
        setupCommands();
    }
    
    @Listener
    public void reloadEvent(GameReloadEvent event) {
    	log.info("Reload event");
    	// This will fail because Sponge does not allow you to re-register
    	// a command that is already registered. Also, Sponge does not
    	// hot-reload plugins, so this event is essentially useless.

        //setupCommands();
    }
    
    /**
     * Helper method for printing messages to the player's console.
     * 
     * It's annoying to have to do Text.of(), plus format strings.
     * 
     * So, this method does all of it for you.
     * 
     * @param src
     * @param message
     * @param args
     */
    private static void message(CommandSource src, String message, Object... args) {
    	if (args.length == 0) {
    		src.sendMessage(Text.of(message));
    	} else {
    		src.sendMessage(Text.of(String.format(message, args)));
    	}
    }
    
    private void setupCommands() {
    	CommandSpec diggy = CommandSpec.builder()
    			.description(Text.of("diggy diggy hole"))
    			.permission("minecraft.command.me").arguments(GenericArguments.optional(GenericArguments.string(Text.of(PLAYER_NAME))))
    			.executor(new CommandExecutor() {
    				@Override
    				public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    					log.info(String.format("name of sender is: %s", src.getName().toLowerCase()));

    					message(src, "%s is digging a hole straight down to hell", src.getName());
    					message(src, "DIGGY DIGGY HOLE");

    					if (src instanceof Player) {
							Player player = (Player) src;
							Location<World> loc = player.getLocation();
							Vector3i pos = loc.getBlockPosition();
							
							message(src, "position %s", pos);
							
							World w = player.getWorld();
							BlockState current = w.getBlock(pos);
							message(src, "block state: %s", current);
							
							
							// Basically, while the current block is not bedrock
							// change it to air, and move the current block down by
							// 1 in the Y direction
							while (current.getType() != BlockTypes.BEDROCK) {
								//log.warn(msg);
								w.setBlockType(pos, BlockTypes.AIR);
								pos = pos.add(0, -1, 0);
								current = w.getBlock(pos);
							}
							
    					}
    					
    					return CommandResult.success();
    				}
    			}).build();
    	
    	// Sponge does not support hot-reloading, so we have to stop/start the server
    	// every time we change a plugin:
    	// https://forums.spongepowered.org/t/how-to-reload-plugins/10507/5
    	//
    	// Thus, it's not necessary to remove/reregister a plugin
    	log.info("Registering 'diggy' command");
    	Sponge.getCommandManager().register(this, diggy, "diggy", "diggy");
    }
}