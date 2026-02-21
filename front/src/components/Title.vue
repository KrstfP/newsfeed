<template>
  <div class="title-container">
    <svg viewBox="0 0 800 200" class="title-svg">

      <defs>
        <linearGradient
  id="animatedGradient"
  x1="-100%"
  y1="0%"
  x2="100%"
  y2="0%"
  gradientUnits="objectBoundingBox"
>
  <stop offset="0%" stop-color="#111111" />
  <stop offset="20%" stop-color="#0065F8" />
  <stop offset="40%" stop-color="#3B82F6" stop-opacity="0.8"/>
  <stop offset="60%" stop-color="#0065F8" />
  <stop offset="80%" stop-color="#2A2A2A" />
  <stop offset="100%" stop-color="#111111" />

  <!-- Animation douce horizontale -->
  <animate
    attributeName="x1"
    values="-100%;100%;-100%"
    dur="18s"
    repeatCount="indefinite"
  />
  <animate
    attributeName="x2"
    values="0%;200%;0%"
    dur="18s"
    repeatCount="indefinite"
  />
</linearGradient>

        <!-- Masque texte -->
        <clipPath id="textClip">
          <text
            x="0%"
            y="50%"
            text-anchor="start"
            dominant-baseline="middle"
            class="title-text"
          >
            Newsfeed
          </text>
        </clipPath>
      </defs>

      <!-- Rectangle rempli par gradient -->
      <rect
        width="100%"
        height="100%"
        fill="url(#animatedGradient)"
        clip-path="url(#textClip)"
      />

    </svg>
  </div>
</template>

<style scoped>
.title-container {
  display: flex;
  justify-content: left;
  margin: 0;
  padding: 0;
}

.title-text {
  font-size: 5rem;
  font-weight: bolder;
  letter-spacing: 1px;
  font-family: sans-serif;
}
</style>