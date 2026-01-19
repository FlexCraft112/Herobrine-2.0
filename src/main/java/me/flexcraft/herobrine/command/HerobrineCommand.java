@Override
public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (!sender.hasPermission("herobrine.use")) {
        sender.sendMessage("§cНет прав");
        return true;
    }

    if (args.length != 1) {
        sender.sendMessage("§cИспользование: /herobrine <ник>");
        return true;
    }

    if (HerobrineNPCSpawner.isActive()) {
        sender.sendMessage("§cХеробрин уже рядом... Он ещё не исчез.");
        return true;
    }

    Player target = Bukkit.getPlayerExact(args[0]);
    if (target == null) {
        sender.sendMessage("§cИгрок не найден");
        return true;
    }

    sender.sendMessage("§7Вы призвали §cХеробрина §7для §f" + target.getName());
    HerobrineNPCSpawner.spawn(plugin, target);

    return true;
}
