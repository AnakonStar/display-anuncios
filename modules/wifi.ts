import { NativeModules, Platform } from 'react-native'

const { WifiManager } = NativeModules as {
  WifiManager?: {
    openWifiSettings?: () => void
    promptWifiConnect?: () => void
    isInternetValidated?: () => Promise<boolean>
  }
}

export function openWifiSettings() {
  if (Platform.OS !== 'android') return

  WifiManager?.openWifiSettings?.()
}

export function promptWifiConnect() {
  if (Platform.OS !== 'android') return

  WifiManager?.promptWifiConnect?.()
}

export async function checkInternetStatus(): Promise<boolean> {
  if (Platform.OS !== 'android') return true

  try {
    const result = await WifiManager?.isInternetValidated?.()
    return Boolean(result)
  } catch (e) {
    return false
  }
}
