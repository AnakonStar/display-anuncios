import { StorageCollectionEnum } from '@/enums/CollectionEnum';
import { UseModalReturn } from '@/hooks/useModal';
import { WifiService } from '@/modules/wifi';
import { LocalStorageService } from '@/utils/localStorage';
import Constants from 'expo-constants';
import { useEffect, useState } from 'react';
import {
  ModalProps,
  Modal as ModalRC,
  Text,
  TouchableOpacity,
  View
} from 'react-native';
import { ModalButton } from '../button/ModalButton';

type MenuModalProps = ModalProps & {
    changeTvCodeModal: UseModalReturn;
}

export function MenuModal({ changeTvCodeModal, ...props }: MenuModalProps) {

  const [actualCode, setActualCode] = useState('');

  const versionName = Constants.expoConfig?.version ?? '';

  async function onLoad() {
    if (actualCode) return;

    try {
      const code = await LocalStorageService.getItem<string>(StorageCollectionEnum.ACTUAL_CODE);

      setActualCode(code || '');
    } catch (error) {
      console.error('Error fetching actual code:', error);
      throw error;
    }
  }

  useEffect(() => {
    onLoad();
  },[])

  return (
    <ModalRC {...props} animationType="fade">
      <TouchableOpacity
        className="absolute inset-0 bg-black/40 bg-opacity-50 flex items-center justify-end w-full h-full"
        onPress={props.onRequestClose}
      >
        <View className="w-full bg-white rounded-t-xl ">
          <View className="p-6">
            <Text
              className="text-2xl mb-3 font-semibold"
              numberOfLines={1}
              ellipsizeMode="tail"
            >
              Código TV: {actualCode}
            </Text>
            <View className="flex-row gap-3 items-center">
              <View className="bg-green-600 h-3 w-3 rounded-full" />
              <Text className="text-gray-600 text-lg">
                Conectado a internet
              </Text>
            </View>
            <View className="flex-row items-center justify-between">
              <Text className="text-gray-400">Display de Anúncios Nuvem3</Text>
              <Text className="text-gray-600">v{versionName}</Text>
            </View>
          </View>
          <ModalButton
            title="Alterar rede Wi-Fi"
            onPress={WifiService.openSettings}
          />
          <ModalButton
            title="Alterar código da TV"
            onPress={changeTvCodeModal.openModal}
          />
          <ModalButton className='bg-gray-300' title="Fechar" onPress={props.onRequestClose} />
        </View>
      </TouchableOpacity>
    </ModalRC>
  );
}
