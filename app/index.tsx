import { useModal } from '@/hooks/useModal';
import { Stack } from 'expo-router';
import { useEffect, useState } from 'react';
import {
  Text,
  View
} from 'react-native';
import { WebView } from 'react-native-webview';
import { WifiService } from '../modules/wifi';

import { Button } from '@/components/ui/button/Button';
import { ExitButton } from '@/components/ui/button/ExitButton';
import { ChangeTvCodeModal } from '@/components/ui/modal/ChangeTvCodeModal';
import { MenuModal } from '@/components/ui/modal/MenuModal';
import Constants from 'expo-constants';

export default function Home() {
  const [isAppFinishedLoading, setIsAppFinishedLoading] = useState(false);
  const [isAppOk, setIsAppOk] = useState(true);
  const [webViewKey, setWebViewKey] = useState(0);
  const menuModal = useModal();
  const changeTvCodeModal = useModal();

  const versionName = Constants.expoConfig?.version ?? '';

  const isAppReady = isAppFinishedLoading && isAppOk;

  function onLayoutReady() {
    setIsAppFinishedLoading(true);
  }

  function onError() {
    setIsAppFinishedLoading(true);
    setIsAppOk(false);
  }

  async function verifyAndReloadIfOnline() {
    const ok = await WifiService.checkInternetStatus();
    if (ok) {
      setIsAppOk(true);
      setIsAppFinishedLoading(true);
      setWebViewKey(k => k + 1);
      return;
    }

    setIsAppFinishedLoading(false);
    WifiService.promptConnect();
  }

  useEffect(() => {
    verifyAndReloadIfOnline();
  }, []);

  return (
    <View className="flex-1">
      <Stack.Screen options={{ title: 'Home' }} />
      {isAppReady ? (
        <>
          <ExitButton onLongPress={menuModal.openModal} />

          <MenuModal
            visible={menuModal.isOpen}
            onRequestClose={menuModal.closeModal}
            transparent
            changeTvCodeModal={changeTvCodeModal}
          />

          <ChangeTvCodeModal
            visible={changeTvCodeModal.isOpen}
            onRequestClose={changeTvCodeModal.closeModal}
            transparent
          />

          <WebView
            key={webViewKey}
            className="flex-1"
            onLoad={onLayoutReady}
            onError={onError}
            source={{ uri: 'https://nuvem3pdv.com.br' }}
          />
        </>
      ) : (
        <View className="flex-1 items-center justify-center bg-white gap-4 p-7">
          <Text className="text-gray-500 text-lg">
            Falha ao carregar o aplicativo
          </Text>
          <Button title="Tentar novamente" onPress={verifyAndReloadIfOnline} />
        </View>
      )}
    </View>
  );
}
