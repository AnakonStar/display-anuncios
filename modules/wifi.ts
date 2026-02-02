import { NativeModules, Platform } from 'react-native'

const { WifiManager } = NativeModules as {
  WifiManager?: {
    openWifiSettings?: () => void
    promptWifiConnect?: () => void
    isInternetValidated?: () => Promise<boolean>
  }
}

function openSettings() {
  if (Platform.OS !== 'android') return

  WifiManager?.openWifiSettings?.()
}

function promptConnect() {
  if (Platform.OS !== 'android') return

  WifiManager?.promptWifiConnect?.()
}

async function checkInternetStatus(): Promise<boolean> {
  if (Platform.OS !== 'android') return true

  try {
    const result = await WifiManager?.isInternetValidated?.()
    return Boolean(result)
  } catch (e) {
    return false
  }
}

export const WifiService = {
  openSettings,
  promptConnect,
  checkInternetStatus,
}