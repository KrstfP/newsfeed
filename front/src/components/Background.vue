<template>
  <div class="bg-lines">
    <svg viewBox="0 0 1200 600" preserveAspectRatio="none">
      <defs>
        <filter id="lineWave">
          <feTurbulence
            type="fractalNoise"
            baseFrequency="0.002 0.01"
            numOctaves="1"
            result="turb"
          >
            <animate
              attributeName="baseFrequency"
              values="0.002 0.01;0.003 0.012;0.002 0.01"
              dur="40s"
              repeatCount="indefinite"
            />
          </feTurbulence>

          <feDisplacementMap
            in="SourceGraphic"
            in2="turb"
            scale="100"
          />
        </filter>
      </defs>

      <g filter="url(#lineWave)">
        <line
          v-for="n in 12"
          :key="n"
          x1="0"
          :y1="n * 50"
          x2="1200"
          :y2="n * 50"
          class="line"
        />
      </g>
    </svg>
  </div>
</template>

<style scoped>
.bg-lines {
  position: fixed;
  inset: 0;
  z-index: -1;
  pointer-events: none;
  opacity: 0.04; /* ULTRA subtil */
}

svg {
  width: 100%;
  height: 100%;
}

.line {
  stroke: black;
  stroke-width: 1;
}
</style>