# Animal Wellness

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen)
![NeoForge](https://img.shields.io/badge/NeoForge-21.1.227-yellow)
![License](https://img.shields.io/badge/License-All%20Rights%20Reserved-red)
---

# Animal Wellness

**Animal Wellness** is a Minecraft mod that adds a complete animal wellness management system, with genetics, breeding,
and new structures to take care of your animals.

## 🌟 Key Features

### Animal Wellness System

Each animal has a tracking system that includes:

- **Affinity**: Increases when you care for animals
- **Age**: Animals grow as time passes
- **Sex**: Male and female for the breeding system
- **Food and Water**: Animals must eat and drink regularly
- **Brushing**: Animals need to be brushed

### Genetic System

- **4 Genetic Traits**: Productivity, Efficiency, Temperament, Resistance
- **Inheritance**: Offspring inherit traits from parents
- **Mutations**: Possibility of genetic mutations in offspring
- **Selection**: Create your perfect animal line!

### Items

#### 🥄 Animal Food

- Special food to fill feed racks

#### 🪥 Animal Brush

- Brush your animals
- Shows particles when near unbrushed animals

#### 🔍 Animal Inspector

- Opens a GUI interface to view all animal statistics

### Blocks

#### 💩 Manure

- Animals produce manure naturally
- Accumulates up to 5 levels
- Can be collected and placed
- Animated fly particles

#### 🌾 Manured Farmland

- Fertilizes soil to improve crop growth
- Created with manure

#### 🥚 Nest

- Block where chickens can lay eggs
- Collects up to 5 eggs

#### 🪵 Feed Rack

- 2-block structure
- Fillable with animal food
- Animals automatically seek the feed rack when hungry
- Right click to displays food level

#### 💧 Water Rack

- 2-block structure
- Fillable with water buckets
- Animals automatically seek the water rack when thirsty
- Right click to displays water level

### Advanced Breeding System

- Male animals seek female partners
- Realistic pregnancy with gestation times
- Offspring inherit parent genetics

### Drop System

- Animals with high affinity drop more resources
- Genetic productivity influences drops
- Configurable drop threshold

## 🎮 How to Play

### 1. Build Your Farm

```
1. Place feed racks and water racks
2. Fill them with food and water
3. Build nests for chickens
4. Add enclosures for animals
```

### 2. Care for Your Animals

```
1. Use the brush on animals
2. Ensure they eat and drink regularly
3. Collect manure to fertilize
4. Use the inspector to check statistics
```

### 3. Selective Breeding

```
1. Use the inspector to see genetics
2. Select the best breeders
3. Let them reproduce naturally
4. Offspring will inherit and improve traits
```

## ⚙️ Configuration

The mod generates an `animal_wellness-server.json` file in the config folder:

```json
{
  "entityList": {
    "whitelist": true,
    "entities": [
      "minecraft:cow",
      "minecraft:pig",
      "minecraft:sheep",
      "minecraft:chicken"
    ]
  },
  "affinity": {
    "affinityRate": 0.1,
    "dropTime": 6000
  },
  "age": {
    "maxAge": 1728000,
    "babyAgeThreshold": 0.04,
    "adultAgeThreshold": 0.8
  },
  "feed": {
    "maxFeed": 12000,
    "maxWater": 12000,
    "searchRange": 10,
    "eatTime": 100
  },
  "manure": {
    "enabled": true,
    "manureTimeMin": 4800,
    "manureTimeMax": 6000
  },
  "brush": {
    "brushTime": 24000
  },
  "breeding": {
    "enabled": true,
    "pregnantTime": 18000,
    "breedingTime": 6000,
    "searchRange": 10,
    "loveTick": 200,
    "affinityThreshold": 0.4
  },
  "drop": {
    "affinityDrop": true,
    "affinityThreshold": 0.3,
    "maxDrop": 10
  },
  "genetics": {
    "enabled": true,
    "mutationAmount": 0.005
  },
  "egg": {
    "eggTime": 18000,
    "searchRange": 10,
    "layTime": 200
  },
  "milk": {
    "milkTime": 18000
  },
  "wool": {
    "woolTime": 18000
  }
}
```

### Main Options

- **entityList**: Configure which animals are managed by the mod (whitelist/blacklist)
- **genetics.enabled**: Enable/disable genetic system
- **breeding.enabled**: Enable/disable breeding system
- **manure.enabled**: Enable/disable manure production
- **drop.affinityDrop**: Enable/disable affinity-based drops

## 🛠️ Commands (Admin)

```
/wellness setAge <entity> <baby|adult|old>
/wellness setGenetic <entity> <trait> <value>
/wellness setSex <entity> <male|female>
```

## 📸 Gallery

*[Add mod screenshots here]*

## 🤝 Support

For bugs, suggestions, or questions:

- Open an issue on GitHub

## 📄 License

All Rights Reserved

## 📝 Changelog

### Version 0.1

- Initial release
- Animal wellness system
- Genetic system with inheritance
- New blocks: Manure, Manured Farmland, Nest, Feed Rack, Water Rack
- New items: Food, Brush, Inspector
- Advanced breeding system
- Full configuration
- Admin commands

---

**Enjoy your farm! 🐄🐷🐑🐔**

---