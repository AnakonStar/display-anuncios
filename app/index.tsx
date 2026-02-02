import { Stack } from 'expo-router';
import { useEffect, useState } from 'react';
import { Text, TouchableOpacity, View } from 'react-native';
import { WebView } from 'react-native-webview';
import { checkInternetStatus, promptWifiConnect } from '../modules/wifi';

export default function Home() {
  const [isAppFinishedLoading, setIsAppFinishedLoading] = useState(false);
  const [isAppOk, setIsAppOk] = useState(true);
  const [webViewKey, setWebViewKey] = useState(0);

  const isAppReady = isAppFinishedLoading && isAppOk;

  function onLayoutReady() {
    setIsAppFinishedLoading(true);
  }

  function onError() {
    setIsAppFinishedLoading(true);
    setIsAppOk(false);
  }

  async function verifyAndReloadIfOnline() {
    const ok = await checkInternetStatus();
    if (ok) {
      setIsAppOk(true);
      setIsAppFinishedLoading(true);
      setWebViewKey(k => k + 1);
      return;
    }

    setIsAppFinishedLoading(false);
    promptWifiConnect();
  }

  useEffect(() => {
    verifyAndReloadIfOnline();
  }, []);

  return (
    <View className="flex-1">
      <Stack.Screen options={{ title: 'Home' }} />
      {isAppReady ? (
        <WebView
          key={webViewKey}
          className="flex-1"
          onLoad={onLayoutReady}
          onError={onError}
          source={{ uri: 'https://nuvem3pdv.com.br' }}
        />
      ) : (
        <View className="flex-1 items-center justify-center bg-white gap-4 p-7">
          <Text className='text-gray-500 text-lg'>Falha ao carregar o aplicativo</Text>
          <TouchableOpacity
            className="w-full h-16 bg-sky-600 flex items-center justify-center rounded-lg"
            onPress={verifyAndReloadIfOnline}
          >
            <Text className="text-white text-lg font-semibold">Tentar novamente</Text>
          </TouchableOpacity>
        </View>
      )}
    </View>
  );
}
