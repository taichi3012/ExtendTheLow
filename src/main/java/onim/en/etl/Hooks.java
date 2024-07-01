package onim.en.etl;

import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import onim.en.etl.event.GetCharWidthEvent;
import onim.en.etl.event.GuiRenderItemEvent;
import onim.en.etl.event.RenderCharAtPosEvent;

public class Hooks {

  public static float onGetCharWidth(float width, char ch) {
    GetCharWidthEvent event = new GetCharWidthEvent(ch, width);
    MinecraftForge.EVENT_BUS.post(event);
    return event.getWidth();
  }

  public static void onRenderCharAtPos(boolean boldStyle) {
    MinecraftForge.EVENT_BUS.post(new RenderCharAtPosEvent(boldStyle));
  }

  public static boolean onHandleTeams(S3EPacketTeams packetIn, Scoreboard scoreboard) {
    ScorePlayerTeam scoreplayerteam;
    if (packetIn.getAction() == 0) {
      scoreplayerteam = scoreboard.createTeam(packetIn.getName());
    } else {
      scoreplayerteam = scoreboard.getTeam(packetIn.getName());
    }

    if (packetIn.getAction() == 0 || packetIn.getAction() == 2) {

      if (scoreplayerteam != null) {

        scoreplayerteam.setTeamName(packetIn.getDisplayName());
        scoreplayerteam.setNamePrefix(packetIn.getPrefix());
        scoreplayerteam.setNameSuffix(packetIn.getSuffix());
        scoreplayerteam.setChatFormat(EnumChatFormatting.func_175744_a(packetIn.getColor()));
        scoreplayerteam.func_98298_a(packetIn.getFriendlyFlags());
        Team.EnumVisible team$enumvisible = Team.EnumVisible.func_178824_a(packetIn.getNameTagVisibility());

        if (team$enumvisible != null) {
          scoreplayerteam.setNameTagVisibility(team$enumvisible);
        }
      }
    }

    if (packetIn.getAction() == 0 || packetIn.getAction() == 3) {
      for (String s : packetIn.getPlayers()) {
        scoreboard.addPlayerToTeam(s, packetIn.getName());
      }
    }

    if (packetIn.getAction() == 4) {
      for (String s1 : packetIn.getPlayers()) {
        scoreboard.removePlayerFromTeam(s1, scoreplayerteam);
      }
    }

    if (packetIn.getAction() == 1) {
      if (scoreplayerteam != null) {
        scoreboard.removeTeam(scoreplayerteam);
      }
    }

    return true;
  }

  public static float getShadowOffset(float offset) {
    if (Prefs.get().betterFont) {
      return .5F;
    }
    return offset;
  }

  public static float getShadowOffsetMinus(float offset) {
    if (Prefs.get().betterFont) {
      return 0F;
    }
    return offset;
  }

  public static void renderItemIntoGUI(ItemStack stack, int x, int y) {
    MinecraftForge.EVENT_BUS.post(new GuiRenderItemEvent(stack, x, y));
  }
}
