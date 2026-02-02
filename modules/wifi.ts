import { NativeModules, Platform } from 'react-native'

const { WifiManager } = NativeModules as {
  WifiManager?: {
    openWifiSettings?: () => void
  }
}

export function openWifiSettings() {
  if (Platform.OS !== 'android') return

  WifiManager?.openWifiSettings?.()
}
