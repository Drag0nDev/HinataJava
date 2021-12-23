package hinata.util.utils;

public class Level {
    private final int level;
    private final int xp;
    private final int percentage;
    private final int neededXp;
    private final int remainingXp;

    public Level(int xp, int levelXp) {
        int level = 0;
        int nextLevelXp = levelXp;
        int remainingXp = xp;

        while (remainingXp > nextLevelXp) {
            level++;
            remainingXp -= nextLevelXp;
            nextLevelXp = levelXp + ((levelXp / 2) * level);
        }

        this.level = level;
        this.xp = xp;
        this.neededXp = nextLevelXp;
        this.remainingXp = remainingXp;
        this.percentage = Math.round((float) remainingXp * 100 / nextLevelXp);
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getPercentage() {
        return percentage;
    }

    public int getNeededXp() {
        return neededXp;
    }

    public int getRemainingXp() {
        return remainingXp;
    }
}
