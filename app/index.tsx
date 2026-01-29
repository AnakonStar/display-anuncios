import { Stack } from 'expo-router';
import { View } from 'react-native';
import { WebView } from 'react-native-webview';

export default function Home() {
  return (
    <View className="flex-1">
      <Stack.Screen options={{ title: 'Home' }} />
      <WebView className="flex-1" source={{ uri: 'https://nuvem3pdv.com.br' }} />
    </View>
  );
}
