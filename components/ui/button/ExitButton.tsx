import { TouchableOpacity, TouchableOpacityProps } from 'react-native';

export function ExitButton(props: TouchableOpacityProps) {
  return (
    <TouchableOpacity
      className="w-24 h-24 absolute top-0 right-0 z-10"
      {...props}
    />
  );
}
