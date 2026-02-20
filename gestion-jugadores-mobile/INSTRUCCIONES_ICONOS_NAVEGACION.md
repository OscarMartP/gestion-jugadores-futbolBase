# 📱 INSTRUCCIONES PARA AGREGAR ICONOS DE NAVEGACIÓN

## ✅ Cambios Realizados

He modificado el código para usar **iconos PNG personalizados** en la navegación inferior de la app móvil.

### Archivos Modificados:
- ✅ [tabs.page.html](src/app/pages/tabs/tabs.page.html) - Reemplaza `ion-icon` por `<img>`
- ✅ [tabs.page.scss](src/app/pages/tabs/tabs.page.scss) - Estilos para los iconos PNG
- ✅ Carpeta creada: `src/assets/img/navigation/`

---

## 📂 PASO 1: Guardar los Iconos PNG

Debes guardar las **4 imágenes PNG** que subiste en la carpeta:

```
gestion-jugadores-mobile/src/assets/img/navigation/
```

### Nombres de Archivo Requeridos:

| Icono | Nombre del Archivo | Descripción |
|-------|-------------------|-------------|
| 👥 Grupo de personas | `jugadores.png` | Icono para la pestaña Jugadores |
| 🛡️ Escudo con balón | `equipos.png` | Icono para la pestaña Equipos |
| ⚽ Campo de fútbol | `partidos.png` | Icono para la pestaña Partidos |
| 📊 Gráfico de líneas | `estadisticas.png` | Icono para la pestaña Estadísticas |

---

## 🎨 Características de los Iconos

Los estilos CSS aplicados:

✅ **Tamaño:** 28x28 píxeles  
✅ **Color:** Blanco (filtro aplicado automáticamente)  
✅ **Opacidad:** 70% normal, 100% cuando está seleccionado  
✅ **Animación:** Escala 1.1x cuando se selecciona el tab  
✅ **Transición suave:** 0.3s ease  

---

## 🚀 PASO 2: Probar la App

Después de guardar las imágenes:

```powershell
cd gestion-jugadores-mobile
ionic serve
```

O si ya está corriendo, la app se recargará automáticamente.

---

## 📱 Resultado Final

La navegación inferior mostrará:
```
  [ICONO]
  Jugadores
```

Con los iconos **sobre** el texto, mejorando la experiencia visual de la app.

---

## 🔧 Solución de Problemas

### Si los iconos NO aparecen:

1. **Verifica los nombres de archivo** (deben ser exactos):
   - `jugadores.png`
   - `equipos.png`
   - `partidos.png`
   - `estadisticas.png`

2. **Verifica la ruta:**
   ```
   src/assets/img/navigation/
   ```

3. **Limpia la caché de Ionic:**
   ```powershell
   ionic build --prod
   ```

### Si los iconos se ven negros:

El filtro CSS los convertirá a blanco automáticamente. Si prefieres usar iconos ya en color blanco, elimina esta línea del [tabs.page.scss](src/app/pages/tabs/tabs.page.scss):

```scss
filter: brightness(0) invert(1); // ← Elimina esta línea
```

---

## 📋 Checklist

- [ ] Guardar `jugadores.png` en `src/assets/img/navigation/`
- [ ] Guardar `equipos.png` en `src/assets/img/navigation/`
- [ ] Guardar `partidos.png` en `src/assets/img/navigation/`
- [ ] Guardar `estadisticas.png` en `src/assets/img/navigation/`
- [ ] Ejecutar `ionic serve` para probar
- [ ] Verificar que los iconos aparezcan correctamente

---

✅ **¡Listo!** Los iconos harán tu app más descriptiva y visual.
