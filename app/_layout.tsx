import '../global.css'

import { Stack } from 'expo-router'
import { useEffect } from 'react'

import { enableImmersiveMode } from '../modules/immersiveMode'

export default function Layout() {
  useEffect(() => {
    enableImmersiveMode()
  }, [])

  return <Stack screenOptions={{ headerShown: false }} />
}
