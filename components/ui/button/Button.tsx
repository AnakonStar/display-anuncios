import { Text, TouchableOpacity } from 'react-native';

export function Button({
  title,
  onPress,
}: {
  title: string;
  onPress: () => void;
}) {
  return (
    <TouchableOpacity
      className="w-full h-16 bg-sky-600 flex items-center justify-center rounded-lg"
      onPress={onPress}
    >
      <Text className="text-white text-lg font-semibold">{title}</Text>
    </TouchableOpacity>
  );
}
