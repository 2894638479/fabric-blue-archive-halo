# Blue Archive Halo – Localization Keys

This file lists all translation keys used in the mod, along with their English meanings.  
They can be used for creating language files (e.g., `en_us.json`, `ru_ru.json`).

---

## 🔧 Autoconfig Keys (used with Mod Menu / Cloth Config)

| Key | Description |
|-----|-------------|
| `modmenu.descriptionTranslation.fabric-blue-archive-halo` | Adds floating Blue Archive‑style halos above beacons. |
| `text.autoconfig.fabric-blue-archive-halo.title` | Blue Archive Halo Settings |
| `text.autoconfig.fabric-blue-archive-halo.option.alpha` | Halo opacity |
| `text.autoconfig.fabric-blue-archive-halo.option.clientCache` | Client cache |
| `text.autoconfig.fabric-blue-archive-halo.option.clientCache.@Tooltip` | Allows rendering halos even if the beacon is outside render distance. |
| `text.autoconfig.fabric-blue-archive-halo.option.combineBeacon` | Beacon merging |
| `text.autoconfig.fabric-blue-archive-halo.option.combineRadius` | Merge radius |
| `text.autoconfig.fabric-blue-archive-halo.option.combineRadius.@Tooltip` | Distance at which identical beacons merge their halos. |
| `text.autoconfig.fabric-blue-archive-halo.option.extraFarPlane` | Extra far plane |
| `text.autoconfig.fabric-blue-archive-halo.option.extraFarPlane.@Tooltip` | Increase this if the halo disappears with shaders enabled. |

---

## 📋 Main UI Keys (used in the in-game settings screens)

| Key | Description |
|-----|-------------|
| `text.fabric-blue-archive-halo.title.main` | Halo Settings |
| `text.fabric-blue-archive-halo.button.choose_level` | Settings by Level |
| `text.fabric-blue-archive-halo.tooltip.choose_level` | Specific configurations for beacons of different levels |
| `text.fabric-blue-archive-halo.slider.base_alpha` | Base opacity |
| `text.fabric-blue-archive-halo.slider.mix_white` | Tint whitening |
| `text.fabric-blue-archive-halo.tooltip.mix_white` | Mixes a little white to make the visual effect brighter |
| `text.fabric-blue-archive-halo.slider.pulse_tail` | Pulse tail length |
| `text.fabric-blue-archive-halo.slider.spacing_mid_alpha` | Spacing mode opacity |
| `text.fabric-blue-archive-halo.tooltip.spacing_mid_alpha` | Opacity of highlighted parts in spacing mode |
| `text.fabric-blue-archive-halo.slider.spacing_alpha` | Spacing gaps opacity |
| `text.fabric-blue-archive-halo.tooltip.spacing_alpha` | Opacity of empty gaps in spacing mode |
| `text.fabric-blue-archive-halo.slider.spacing_count` | Number of intervals: %s |

---

## 📂 Level Selection Screen Keys

| Key | Description |
|-----|-------------|
| `text.fabric-blue-archive-halo.title.level_choose` | Settings by Level |
| `text.fabric-blue-archive-halo.button.beacon_level` | Beacon level %s (Rings: %s) |
| `text.fabric-blue-archive-halo.tooltip.delete_level` | Delete configuration for this beacon level |
| `text.fabric-blue-archive-halo.button.add_level` | ➕ Add new beacon level |

---

## 🔘 Level Configuration Screen Keys (per‑level ring settings)

| Key | Description |
|-----|-------------|
| `text.fabric-blue-archive-halo.title.level_config` | Beacon level %s (Rings: %s) |
| `text.fabric-blue-archive-halo.slider.color_spacing` | Color sampling interval: %s |
| `text.fabric-blue-archive-halo.tooltip.color_spacing` | The halo is coloured according to the beacon beam. From outer to inner rings it takes colors 1,2,3,4... of the beam. If interval = 2, it takes colors 2,4,6,8... etc. |
| `text.fabric-blue-archive-halo.slider.height` | Halo height: %s |
| `text.fabric-blue-archive-halo.button.add_ring` | ➕ |
| `text.fabric-blue-archive-halo.button.remove_ring` | ➖ |
| `text.fabric-blue-archive-halo.slider.radius` | Radius: %s |
| `text.fabric-blue-archive-halo.slider.width` | Width: %s |
| `text.fabric-blue-archive-halo.slider.speed` | Speed: %s |

---

## ⏯️ Preview & Pause Keys

| Key | Description |
|-----|-------------|
| `text.fabric-blue-archive-halo.button.paused` | Paused |
| `text.fabric-blue-archive-halo.button.running` | Running |
| `text.fabric-blue-archive-halo.tooltip.paused` | Show dynamic effects (will start game time) |
| `text.fabric-blue-archive-halo.button.preview` | Preview |
| `text.fabric-blue-archive-halo.title.previewing` | Preview mode, adjust your view angle in the game yourself |
| `text.fabric-blue-archive-halo.tooltip.preview` | Hide UI for convenient preview (in‑game only) |

---

## ✨ Ring Effect Keys (names and descriptions)

| Key | Description |
|-----|-------------|
| `text.fabric-blue-archive-halo.effect.pulse` | Pulse |
| `text.fabric-blue-archive-halo.effect.spacing` | Spacing |
| `text.fabric-blue-archive-halo.effect.flat` | Flat |
| `text.fabric-blue-archive-halo.effect.static` | Static |
| `text.fabric-blue-archive-halo.effect.unknown` | Unknown |
| `text.fabric-blue-archive-halo.desc.pulse` | Pulsing rotation effect. Highlighted part is controlled by opacity; at the peak of pulse opacity = 1. |
| `text.fabric-blue-archive-halo.desc.spacing` | Like a dashed line, blinking with intervals. |
| `text.fabric-blue-archive-halo.desc.flat` | Only semi‑transparent background colour, no other effects. |
| `text.fabric-blue-archive-halo.desc.static` | Only opaque background colour, no other effects. |
| `text.fabric-blue-archive-halo.desc.unknown` | Unknown effect |

---

## 🧩 Additional Keys

| Key | Description |
|-----|-------------|
| `text.fabric-blue-archive-halo.slider.ring_count` | Number of active rings: %s |
| `text.fabric-blue-archive-halo.tooltip.non_vanilla_level` | Levels above 4 require server‑side mods |

---

> **Note:** All `%s` placeholders are replaced with numeric or string values at runtime.
