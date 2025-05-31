package larina;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.stream.Collectors;

public class IslandSimulation {
    public static void main(String[] args) {
        Island island = new Island(Settings.ISLAND_WIDTH, Settings.ISLAND_HEIGHT);
        initializeWithEntities(island);
        island.startSimulation();
    }

    private static void initializeWithEntities(Island island) {
        Random random = new Random();

        // Добавляем растения
        for (int i = 0; i < Settings.INITIAL_COUNTS[Settings.PLANT]; i++) {
            int x = random.nextInt(Settings.ISLAND_WIDTH);
            int y = random.nextInt(Settings.ISLAND_HEIGHT);
            island.getLocation(x, y).addEntity(new Plant());
        }

        // Добавляем животных
        for (int type = 0; type < Settings.INITIAL_COUNTS.length - 1; type++) {
            for (int i = 0; i < Settings.INITIAL_COUNTS[type]; i++) {
                int x = random.nextInt(Settings.ISLAND_WIDTH);
                int y = random.nextInt(Settings.ISLAND_HEIGHT);
                island.getLocation(x, y).addEntity(AnimalFactory.createAnimal(type));
            }
        }
    }
}

class AnimalFactory {
    public static Animal createAnimal(int type) {
        switch (type) {
            case Settings.WOLF: return new Wolf();
            case Settings.BOA: return new Boa();
            case Settings.FOX: return new Fox();
            case Settings.BEAR: return new Bear();
            case Settings.EAGLE: return new Eagle();
            case Settings.HORSE: return new Horse();
            case Settings.DEER: return new Deer();
            case Settings.RABBIT: return new Rabbit();
            case Settings.MOUSE: return new Mouse();
            case Settings.GOAT: return new Goat();
            case Settings.SHEEP: return new Sheep();
            case Settings.BOAR: return new Boar();
            case Settings.BUFFALO: return new Buffalo();
            case Settings.DUCK: return new Duck();
            case Settings.CATERPILLAR: return new Caterpillar();
            default: throw new IllegalArgumentException("Unknown animal type: " + type);
        }
    }
}

class Settings {
    // Параметры острова
    public static final int ISLAND_WIDTH = 20;
    public static final int ISLAND_HEIGHT = 15;
    public static final int ITERATION_DELAY = 500;

    // Идентификаторы животных
    public static final int WOLF = 0;
    public static final int BOA = 1;
    public static final int FOX = 2;
    public static final int BEAR = 3;
    public static final int EAGLE = 4;
    public static final int HORSE = 5;
    public static final int DEER = 6;
    public static final int RABBIT = 7;
    public static final int MOUSE = 8;
    public static final int GOAT = 9;
    public static final int SHEEP = 10;
    public static final int BOAR = 11;
    public static final int BUFFALO = 12;
    public static final int DUCK = 13;
    public static final int CATERPILLAR = 14;
    public static final int PLANT = 15;

    //  количество существ
    public static final int[] INITIAL_COUNTS = {
            20,  // Wolf
            15,  // Boa
            30,  // Fox
            5,   // Bear
            10,  // Eagle
            20,  // Horse
            25,  // Deer
            50,  // Rabbit
            100, // Mouse
            25,  // Goat
            30,  // Sheep
            20,  // Boar
            10,  // Buffalo
            40,  // Duck
            200, // Caterpillar
            500  // Plant
    };

    // Изменение здоровья
    public static final double HEALTH_DECAY = 10;
    public static final double REPRODUCE_HEALTH_THRESHOLD = 70;
    public static final double REPRODUCE_COST = 20;

    // Поведение
    public static final int MIN_PACK_SIZE = 3;
    public static final double PACK_HUNTING_BONUS = 0.3;

    // Рельеф
    public static final double RIVER_PROBABILITY = 0.2;
    public static final int RIVER_WIDTH = 3;

    // Графика
    public static final char[] MAP_SYMBOLS = {
            'W', // Wolf
            'S', // Boa (snake)
            'F', // Fox
            'B', // Bear
            'E', // Eagle
            'H', // Horse
            'D', // Deer
            'R', // Rabbit
            'M', // Mouse
            'G', // Goat
            'P', // Sheep (sheep - P for pasture)
            'A', // Boar
            'U', // Buffalo
            'K', // Duck
            'C', // Caterpillar
            '*', // Plant
            '~', // Water
            '.'  // Empty
    };

    // Матрица вероятностей поедания
    public static final double[][] CHANCE_TO_EAT = {
            {-1, 0, 0, 0, 0, 10, 15, 60, 80, 60, 70, 15, 10, 40, 0, 0},
            {0, -1, 15, 0, 0, 0, 0, 20, 40, 0, 0, 0, 0, 10, 0, 0},
            {0, 0, -1, 0, 0, 0, 0, 70, 90, 0, 0, 0, 0, 60, 40, 0},
            {0, 80, 0, -1, 0, 40, 80, 80, 90, 70, 70, 50, 20, 10, 0, 0},
            {0, 0, 10, 0, -1, 0, 0, 90, 90, 0, 0, 0, 0, 80, 0, 0},
            {0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 100},
            {0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 100},
            {0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 100},
            {0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 90, 100},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 100},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 100},
            {0, 0, 0, 0, 0, 0, 0, 0, 50, 0, 0, -1, 0, 0, 90, 100},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 100},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 90, 100},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 100},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1}
    };

    // Параметры животных: [макс. здоровье, макс. на клетке, скорость, потребность в еде, умение плавать (0/1)]
    public static final double[][] ANIMAL_PROPS = {
            {50, 30, 3, 8, 0},     // Wolf (не умеет плавать)
            {15, 30, 1, 3, 1},     // Boa (умеет плавать)
            {8, 30, 2, 2, 0},      // Fox
            {500, 5, 2, 80, 1},    // Bear (умеет плавать)
            {6, 20, 3, 1, 1},      // Eagle (летает над водой)
            {400, 20, 4, 60, 1},   // Horse (умеет плавать)
            {300, 20, 4, 50, 1},   // Deer (умеет плавать)
            {2, 150, 2, 0.45, 0},  // Rabbit
            {0.05, 500, 1, 0.01, 0},// Mouse
            {60, 140, 3, 10, 1},   // Goat (умеет плавать)
            {70, 140, 3, 15, 0},   // Sheep
            {400, 50, 2, 50, 1},   // Boar (умеет плавать)
            {700, 10, 3, 100, 1},  // Buffalo (умеет плавать)
            {1, 200, 4, 0.15, 1},  // Duck (умеет плавать)
            {0.01, 1000, 0, 0, 0}, // Caterpillar
            {1, 200, 0, 0, 0}      // Plant
    };
}

abstract class Entity {
    protected Location location;
    protected volatile boolean alive = true;
    protected double foodValue;
    protected int typeIndex;

    public abstract void performAction();
    public boolean isAlive() { return alive; }
    public int getTypeIndex() { return typeIndex; }

    public void setLocation(Location location) {
        this.location = location;
    }

    public char getMapSymbol() {
        return Settings.MAP_SYMBOLS[typeIndex];
    }
}

class Plant extends Entity {
    public Plant() {
        this.typeIndex = Settings.PLANT;
        this.foodValue = Settings.ANIMAL_PROPS[Settings.PLANT][0];
    }

    @Override
    public void performAction() {
        // Растения не выполняют действий
    }
}

abstract class Animal extends Entity {
    protected double health;
    protected final double maxHealth;
    protected final int maxMoveDistance;
    protected final double foodRequirement;
    protected final boolean canSwim;

    public Animal(int typeIndex) {
        this.typeIndex = typeIndex;
        this.maxHealth = Settings.ANIMAL_PROPS[typeIndex][0];
        this.health = maxHealth;
        this.foodValue = maxHealth * 0.5;
        this.maxMoveDistance = (int) Settings.ANIMAL_PROPS[typeIndex][2];
        this.foodRequirement = Settings.ANIMAL_PROPS[typeIndex][3];
        this.canSwim = Settings.ANIMAL_PROPS[typeIndex][4] == 1;
    }

    protected void move() {
        if (location == null || !alive || maxMoveDistance == 0) return;

        var newLocation = getLocation();
        if (newLocation != location) {
            // Проверка возможности перемещения через реку
            if (newLocation.isRiver() && !canSwim) {
                return; // Животное не может перейти реку
            }

            location.removeEntity(this);
            newLocation.addEntity(this);
            location = newLocation;
        }
    }

    private Location getLocation() {
        Island island = location.getIsland();
        Random random = new Random();
        int newX = location.getX() + random.nextInt(2 * maxMoveDistance + 1) - maxMoveDistance;
        int newY = location.getY() + random.nextInt(2 * maxMoveDistance + 1) - maxMoveDistance;

        // Проверка границ острова
        newX = Math.max(0, Math.min(newX, island.getWidth() - 1));
        newY = Math.max(0, Math.min(newY, island.getHeight() - 1));

        Location newLocation = island.getLocation(newX, newY);
        return newLocation;
    }

    protected void eat(Entity food) {
        if (food != null && food.isAlive()) {
            double chance = Settings.CHANCE_TO_EAT[this.typeIndex][food.getTypeIndex()];
            if (chance >= 0 && chance >= new Random().nextDouble() * 100) {
                double nutrition = Math.min(food.foodValue, foodRequirement);
                health = Math.min(maxHealth, health + nutrition);
                food.alive = false;
                return;
            }
        }
        // Если не удалось поесть, теряем здоровье
        health -= Settings.HEALTH_DECAY * 0.2;
    }

    protected void reproduce() {
        if (health > Settings.REPRODUCE_HEALTH_THRESHOLD) {
            try {
                Animal child = this.getClass().getDeclaredConstructor().newInstance();
                health -= Settings.REPRODUCE_COST;
                location.addEntity(child);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void findFood() {
        List<Entity> potentialFood = new ArrayList<>(location.getEntities());
        Collections.shuffle(potentialFood);

        for (Entity entity : potentialFood) {
            if (entity != this && entity.isAlive() &&
                    Settings.CHANCE_TO_EAT[typeIndex][entity.getTypeIndex()] > 0) {
                eat(entity);
                return;
            }
        }
    }

    protected void standardBehavior() {
        if (!alive) return;

        // Поиск еды
        findFood();

        // Перемещение
        if (new Random().nextDouble() < 0.7) {
            move();
        }

        // Размножение
        if (new Random().nextDouble() < 0.1) {
            reproduce();
        }

        // Потеря здоровья
        health -= Settings.HEALTH_DECAY;
        if (health <= 0) alive = false;
    }
}

// Реализации всех животных
class Wolf extends Animal {
    public Wolf() { super(Settings.WOLF); }

    @Override
    public void performAction() {
        if (!alive) return;

        // Стайная охота
        if (tryPackHunting()) {
            // Охота в стае успешна
        } else {
            // Индивидуальная охота
            findFood();
        }

        // Перемещение стаей
        if (shouldMoveAsPack()) {
            moveWithPack();
        } else {
            move();
        }

        // Размножение
        if (new Random().nextDouble() < 0.1) {
            reproduce();
        }

        // Потеря здоровья
        health -= Settings.HEALTH_DECAY;
        if (health <= 0) alive = false;
    }

    private boolean tryPackHunting() {
        List<Wolf> pack = location.getEntities().stream()
                .filter(e -> e instanceof Wolf && e.isAlive())
                .map(e -> (Wolf) e)
                .collect(Collectors.toList());

        if (pack.size() >= Settings.MIN_PACK_SIZE) {
            // Совместная охота
            for (Entity entity : location.getEntities()) {
                if (entity != this && entity.isAlive() &&
                        Settings.CHANCE_TO_EAT[typeIndex][entity.getTypeIndex()] > 0) {

                    double chance = Settings.CHANCE_TO_EAT[typeIndex][entity.getTypeIndex()];
                    double packChance = chance * (1 + Settings.PACK_HUNTING_BONUS * pack.size());

                    if (packChance >= new Random().nextDouble() * 100) {
                        eat(entity);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean shouldMoveAsPack() {
        long packSize = location.getEntities().stream()
                .filter(e -> e instanceof Wolf && e.isAlive())
                .count();
        return packSize >= Settings.MIN_PACK_SIZE;
    }

    private void moveWithPack() {
        // Лидер стаи выбирает направление
        Wolf leader = location.getEntities().stream()
                .filter(e -> e instanceof Wolf && e.isAlive())
                .map(e -> (Wolf) e)
                .max(Comparator.comparingDouble(w -> w.health))
                .orElse(this);

        if (leader == this) {
            move();
        }
    }
}

class Boa extends Animal {
    public Boa() { super(Settings.BOA); }
    @Override public void performAction() { standardBehavior(); }
}
class Fox extends Animal {
    public Fox() { super(Settings.FOX); }
    @Override public void performAction() { standardBehavior(); }
}
class Bear extends Animal {
    public Bear() { super(Settings.BEAR); }
    @Override public void performAction() { standardBehavior(); }
}
class Eagle extends Animal {
    public Eagle() { super(Settings.EAGLE); }
    @Override public void performAction() { standardBehavior(); }
}
class Horse extends Animal {
    public Horse() { super(Settings.HORSE); }
    @Override public void performAction() { standardBehavior(); }
}
class Deer extends Animal {
    public Deer() { super(Settings.DEER); }
    @Override public void performAction() { standardBehavior(); }
}
class Rabbit extends Animal {
    public Rabbit() { super(Settings.RABBIT); }
    @Override public void performAction() { standardBehavior(); }
}
class Mouse extends Animal {
    public Mouse() { super(Settings.MOUSE); }
    @Override public void performAction() { standardBehavior(); }
}
class Goat extends Animal {
    public Goat() { super(Settings.GOAT); }
    @Override public void performAction() { standardBehavior(); }
}
class Sheep extends Animal {
    public Sheep() { super(Settings.SHEEP); }
    @Override public void performAction() { standardBehavior(); }
}
class Boar extends Animal {
    public Boar() { super(Settings.BOAR); }
    @Override public void performAction() { standardBehavior(); }
}
class Buffalo extends Animal {
    public Buffalo() { super(Settings.BUFFALO); }
    @Override public void performAction() { standardBehavior(); }
}
class Duck extends Animal {
    public Duck() { super(Settings.DUCK); }
    @Override public void performAction() { standardBehavior(); }
}
class Caterpillar extends Animal {
    public Caterpillar() { super(Settings.CATERPILLAR); }
    @Override public void performAction() {
        if (!alive) return;
        findFood();
        if (new Random().nextDouble() < 0.3) reproduce();
        health -= Settings.HEALTH_DECAY * 0.5;
        if (health <= 0) alive = false;
    }
}

class Island {
    private final Location[][] locations;
    private final int width;
    private final int height;
    private final ExecutorService executor;
    private volatile boolean running;
    private int epoch = 0;

    public Island(int width, int height) {
        this.width = width;
        this.height = height;
        this.locations = new Location[width][height];
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        initializeLocations();
        generateRivers();
    }

    private void initializeLocations() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                locations[x][y] = new Location(x, y, this);
            }
        }
    }

    private void generateRivers() {
        Random random = new Random();
        // Вертикальные реки
        for (int i = 0; i < width * Settings.RIVER_PROBABILITY; i++) {
            int riverX = random.nextInt(width - Settings.RIVER_WIDTH);
            for (int w = 0; w < Settings.RIVER_WIDTH; w++) {
                for (int y = 0; y < height; y++) {
                    if (riverX + w < width) {
                        locations[riverX + w][y].setRiver(true);
                    }
                }
            }
        }

        // Горизонтальные реки
        for (int i = 0; i < height * Settings.RIVER_PROBABILITY; i++) {
            int riverY = random.nextInt(height - Settings.RIVER_WIDTH);
            for (int w = 0; w < Settings.RIVER_WIDTH; w++) {
                for (int x = 0; x < width; x++) {
                    if (riverY + w < height) {
                        locations[x][riverY + w].setRiver(true);
                    }
                }
            }
        }
    }

    public Location getLocation(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return locations[x][y];
        }
        return null;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void startSimulation() {
        running = true;
        System.out.println("Simulation started!");

        while (running && epoch < 100) {
            epoch++;
            System.out.println("\n--- Epoch " + epoch + " ---");
            runEpoch();
            printStatistics();
            printMap();

            try {
                Thread.sleep(Settings.ITERATION_DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
        executor.shutdown();
        System.out.println("Simulation finished!");
    }

    private void runEpoch() {
        List<Future<?>> futures = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int fx = x;
                final int fy = y;
                futures.add(executor.submit(() -> locations[fx][fy].processEntities()));
            }
        }

        // Ожидание завершения всех задач
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void printStatistics() {
        Map<Class<?>, Integer> counts = new HashMap<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (Entity entity : locations[x][y].getEntities()) {
                    if (entity.isAlive()) {
                        counts.merge(entity.getClass(), 1, Integer::sum);
                    }
                }
            }
        }

        System.out.println("Island statistics:");
        counts.forEach((clazz, count) ->
                System.out.println(clazz.getSimpleName() + ": " + count));
    }

    private void printMap() {
        System.out.println("\nIsland Map:");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Location loc = locations[x][y];
                char symbol;

                if (loc.isRiver()) {
                    symbol = Settings.MAP_SYMBOLS[16]; // Water symbol
                } else {
                    List<Entity> entities = loc.getEntities().stream()
                            .filter(Entity::isAlive)
                            .collect(Collectors.toList());

                    if (entities.isEmpty()) {
                        symbol = Settings.MAP_SYMBOLS[17]; // Empty
                    } else {
                        // Находим наиболее значимое существо
                        Entity topEntity = entities.stream()
                                .min(Comparator.comparingInt(e ->
                                        e instanceof Plant ? 2 :
                                                e instanceof Caterpillar ? 1 : 0))
                                .orElse(entities.get(0));

                        symbol = topEntity.getMapSymbol();
                    }
                }
                System.out.print(symbol + " ");
            }
            System.out.println();
        }
    }
}

class Location {
    private final List<Entity> entities = new ArrayList<>();
    private final int x, y;
    private final Island island;
    private final Lock lock = new ReentrantLock();
    private boolean isRiver = false;

    public Location(int x, int y, Island island) {
        this.x = x;
        this.y = y;
        this.island = island;
    }

    public void setRiver(boolean isRiver) {
        this.isRiver = isRiver;
    }

    public boolean isRiver() {
        return isRiver;
    }

    public void processEntities() {
        lock.lock();
        try {
            // Создаем копию для безопасной итерации
            List<Entity> copy = new ArrayList<>(entities);
            for (Entity entity : copy) {
                if (entity.isAlive()) {
                    entity.performAction();
                }
            }

            // Удаляем мертвые сущности
            entities.removeIf(e -> !e.isAlive());
        } finally {
            lock.unlock();
        }
    }

    public void addEntity(Entity entity) {
        lock.lock();
        try {
            entity.setLocation(this);
            entities.add(entity);
        } finally {
            lock.unlock();
        }
    }

    public void removeEntity(Entity entity) {
        lock.lock();
        try {
            entities.remove(entity);
        } finally {
            lock.unlock();
        }
    }

    public List<Entity> getEntities() {
        lock.lock();
        try {
            return new ArrayList<>(entities);
        } finally {
            lock.unlock();
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public Island getIsland() { return island; }
}