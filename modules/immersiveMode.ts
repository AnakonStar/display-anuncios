import { NativeModules, Platform } from 'react-native'

const { ImmersiveMode } = NativeModules as {
  ImmersiveMode?: {
    enable?: () => void
  }
}

export function enableImmersiveMode() {
  if (Platform.OS !== 'android') return

  ImmersiveMode?.enable?.()
}
