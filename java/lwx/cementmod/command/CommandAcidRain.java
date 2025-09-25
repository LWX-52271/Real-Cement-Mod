package lwx.cementmod.command;

import lwx.cementmod.CementMod;
import lwx.cementmod.world.AcidRainHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandAcidRain extends CommandBase {

    @Override
    public String getName() {
        return "acidrain";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/acidrain [duration] - 触发酸雨，可选的持续时间(秒)";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = sender.getEntityWorld();

        if (world.isRemote) {
            return; // 只在服务器端执行
        }

        // 检查酸雨是否启用
        if (!CementMod.isAcidRainEnabled()) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "酸雨功能未启用!"));
            return;
        }

        int duration = 600; // 默认持续时间10分钟(600秒)

        if (args.length > 0) {
            try {
                duration = Integer.parseInt(args[0]);
                // 限制持续时间在合理范围内
                duration = Math.max(10, Math.min(3600, duration)); // 10秒到1小时
            } catch (NumberFormatException e) {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "无效的持续时间! 请输入一个数字。"));
                return;
            }
        }

        // 设置下雨和雷暴
        world.getWorldInfo().setRaining(true);
        world.getWorldInfo().setThundering(true);
        world.getWorldInfo().setRainTime(duration * 20); // 转换为ticks
        world.getWorldInfo().setThunderTime(duration * 20);

        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "已触发酸雨，持续时间: " + duration + "秒"));
        AcidRainHandler.setCommandTriggeredRain(world.provider.getDimension());
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // 需要OP权限
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "60", "300", "600", "1200");
        }
        return Collections.emptyList();
    }
}