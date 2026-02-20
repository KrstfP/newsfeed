<template>
  <div class="title-wrapper">
    <svg viewBox="0 0 800 200" class="title-svg">
      <defs>
        <filter id="glitch">
          <feTurbulence
            type="fractalNoise"
            baseFrequency="0.0001"
            numOctaves="2"
            result="turb"
          />
          <feDisplacementMap
            in="SourceGraphic"
            in2="turb"
            scale="0"
            id="displacement"
          />
        </filter>
      </defs>

      <text
        x="50%"
        y="50%"
        text-anchor="middle"
        dominant-baseline="middle"
        class="title-text"
        filter="url(#glitch)"
        @mouseenter="activate"
        @mouseleave="deactivate"
      >
        newsfeed
      </text>
    </svg>
  </div>
</template>

<script setup>
const activate = () => {
  document
    .getElementById("displacement")
    ?.setAttribute("scale", "12")
}

const deactivate = () => {
  document
    .getElementById("displacement")
    ?.setAttribute("scale", "0")
}
</script>

<style scoped>
.title-wrapper {
  display: flex;
  justify-content: center;
  margin: 40px 0;
}

.title-svg {
  width: 400px;
  height: 100px;
}

.title-text {
  font-size: 48px;
  font-weight: 700;
  letter-spacing: 2px;
  font-family: sans-serif;
  fill: black;
  cursor: default;
}
</style>